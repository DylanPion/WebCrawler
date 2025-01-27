package com.crawler.fetcher.service;

import com.crawler.fetcher.dto.WebPageDTO;
import com.crawler.fetcher.entities.UrlCrawled;
import com.crawler.fetcher.repositories.UrlCrawledRepository;
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

    // Méthode thread-safe et traitée en paralèlle
    public void startCrawling(String url) throws InterruptedException, IOException {
        try {
            logger.info("URL received from Kafka : {}", url);

            rateLimterService.throttleRequests();

            // Vérifie le respect du robots.txt
            Map<String, Map<String, List<String>>> robotTxtParsedMap = robotTxtService.parseRobotsTxt(url);
            if (!urlService.isUrlAllowedForScrapping(url, "*", robotTxtParsedMap)) {
                logger.warn("This page is not allowed for scrapping : {}", url);
                return;
            }

            // Vérification de la date du dernier crawl
            synchronized (this) { // Synchronisation pour éviter des accès concurrents à la DB
                if (redisService.hasBeenCrawledRecently(url)) {
                    logger.info("This page has already been crawled recently : {}", url);
                    return;
                }
            }

            // Requête HTTP et extraction des données
            Document doc = Jsoup.connect(url).timeout(1500).get();
            String title = doc.title();
            String text = doc.body().text();
            Map<String, String> metaData = extractMetaData(doc);

            // Récupération et envoi des liens valides
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String linkHref = link.attr("abs:href");
                try {
                    URI baseUri = new URI(url);
                    URI resolvedUri = baseUri.resolve(linkHref);
                    String absoluteUrl = resolvedUri.toString();

                    if (urlService.isValidUrl(absoluteUrl)) {
                        kafkaProducerService.sendUrlToFetch(absoluteUrl);
                        logger.info("Url sent to Kafka for fetching: {}", absoluteUrl);
                    }
                } catch (URISyntaxException e) {
                    logger.warn("Invalid URL found in link: {}. Error: {}", linkHref, e.getMessage());
                }
            }

            // Sauvegarde l'utl dans la base de données une fois crawlé
            synchronized (this) { // Synchronisation pour garantir que la sauvegarde est thread-safe
                urlCrawledRepository.save(new UrlCrawled(url));
            }

            // Envoi au parseur
            WebPageDTO webPageDTO = new WebPageDTO();
            webPageDTO.setUrl(url);
            webPageDTO.setTitle(title);
            webPageDTO.setText(text);
            webPageDTO.setMetaData(metaData);
            // TODO Envoyer les données au parseur via WebClient ou autre
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == 403 || e.getStatusCode() == 429 || e.getStatusCode() == 503) {
                logger.warn("Temporary error {} on URL {}, retrying...", e.getStatusCode(), url);
                retryWithBackoff(url, 3); // 3 retries avec backoff exponentiel
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

    private boolean retryWithBackoff(String url, int retries) {
        int delay = 1000; // 1 seconde initiale
        for (int i = 1; i <= retries; i++) {
            try {
                Thread.sleep(delay);
                kafkaProducerService.sendUrlToFetch(url); // Réinsérer dans Kafka
                logger.info("Retry successful for URL {} on attempt {}", url, i);
                return true; // Succès
            } catch (InterruptedException interruptedException) {
                logger.error("Retry interrupted for URL {}: {}", url, interruptedException.getMessage());
            }
            delay *= 2; // Backoff exponentiel
        }
        logger.error("Failed to process URL {} after {} retries.", url, retries);
        return false; // Échec
    }
}
