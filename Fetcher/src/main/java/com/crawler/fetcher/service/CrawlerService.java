package com.crawler.fetcher.service;

import com.crawler.fetcher.dto.WebPageDTO;
import com.crawler.fetcher.entities.UrlCrawled;
import com.crawler.fetcher.repositories.UrlCrawledRepository;
import com.crawler.fetcher.service.FileService;
import lombok.RequiredArgsConstructor;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CrawlerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final RateLimiterService rateLimterService;
    private final UrlService urlService;
    private final RedisService redisService;
    private final KafkaProducerService kafkaProducerService;
    private final UrlCrawledRepository urlCrawledRepository;
    private final RobotTxtService robotTxtService;

    private final FileService fileService;

    // Thread-safe method that can be processed in parallel
    public void startCrawling(String url) throws InterruptedException, IOException {
        try {

            logger.info("URL received from Kafka : {}", url);

            // Throttle requests to avoid overwhelming the server
            rateLimterService.throttleRequests();

            // Check robots.txt compliance
            Map<String, Map<String, List<String>>> robotTxtParsedMap = robotTxtService.parseRobotsTxt(url);
            // Check if the URL is allowed to be crawled based on robots.txt rules
            if (!urlService.isUrlAllowedForScrapping(url, "*", robotTxtParsedMap)) {
                logger.warn("This page is not allowed for scrapping : {}", url);
                return;
            }

            // Check last crawl date via redis
            synchronized (this) { // Synchronization to avoid concurrent DB access
                if (redisService.hasBeenCrawledRecently(url)) {
                    logger.info("This page has already been crawled recently : {}", url);
                    return;
                }
            }

            // HTTP request and data extraction
            Document doc = Jsoup.connect(url).timeout(1500).get();
            String title = doc.title();
            String text = doc.body().text();
            Map<String, String> metaData = extractMetaData(doc);

            // Get and send valid links
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String linkHref = link.attr("abs:href");
                try {
                    URI baseUri = new URI(url);
                    URI resolvedUri = baseUri.resolve(linkHref);
                    String absoluteUrl = resolvedUri.toString();

                    // Check if the URL is valid and allowed to be crawled
                    if (urlService.isValidUrl(absoluteUrl)) {
                        kafkaProducerService.sendUrlToFetch(absoluteUrl);
                        logger.info("Url sent to Kafka for fetching: {}", absoluteUrl);
                    }
                } catch (URISyntaxException e) {
                    logger.warn("Invalid URL found in link: {}. Error: {}", linkHref, e.getMessage());
                }
            }

            // Save the URL in database once crawled
            synchronized (this) { // Synchronization to ensure thread-safe saving
                urlCrawledRepository.save(new UrlCrawled(url));
            }

            // Send to parser
            WebPageDTO webPageDTO = new WebPageDTO();
            webPageDTO.setUrl(url);
            webPageDTO.setTitle(title);
            webPageDTO.setText(text);
            webPageDTO.setMetaData(metaData);

            // Send the crawled content to parser
            kafkaProducerService.sendUrlToParse(webPageDTO);
            // Save the crawled content to a file
            String filePath = fileService.saveCrawledContent(url, text);
            // Envoie le chemin du fichier à Kafka
            kafkaProducerService.sendFilePathToParse(filePath);
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == 403 || e.getStatusCode() == 429 || e.getStatusCode() == 503) {
                logger.warn("Temporary error {} on URL {}, retrying...", e.getStatusCode(), url);
                retryWithBackoff(url, 3); // 3 retries with exponential backoff
            } else {
                logger.error("HTTP error {} for URL {}: {}", e.getStatusCode(), url, e.getMessage());
            }
        } catch (SocketTimeoutException e) {
            logger.warn("Timeout for URL {}, retrying...", url);
            retryWithBackoff(url, 3);
        } catch (Exception e) {
            logger.error("Unexpected error for URL {}: {}", url, e.getMessage());
        }
    }

    // Extract meta data from the document
    private Map<String, String> extractMetaData(Document doc) {
        Map<String, String> metaData = new HashMap<>();
        Elements metaTags = doc.select("meta");
        for (Element meta : metaTags) {
            String name = meta.attr("name");
            String content = meta.attr("content");
            if (!name.isEmpty()) {
                metaData.put(name, content);
            }
        }
        return metaData;
    }

    // Retry with exponential backoff
    private boolean retryWithBackoff(String url, int retries) {
        int delay = 1000; // Initial 1 second delay
        for (int i = 1; i <= retries; i++) {
            try {
                Thread.sleep(delay);
                kafkaProducerService.sendUrlToFetch(url); // Reinsert into Kafka
                logger.info("Retry successful for URL {} on attempt {}", url, i);
                return true; // Success
            } catch (InterruptedException interruptedException) {
                logger.error("Retry interrupted for URL {}: {}", url, interruptedException.getMessage());
            }
            delay *= 2; // Exponential backoff
        }
        logger.error("Failed to process URL {} after {} retries.", url, retries);
        return false; // Failure
    }
}
