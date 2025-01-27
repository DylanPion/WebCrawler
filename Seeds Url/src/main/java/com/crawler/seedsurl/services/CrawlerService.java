package com.crawler.seedsurl.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrawlerService {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerService.class);
    private final KafkaProducerService kafkaProducerService;
    private static final String[] INVALID_EXTENSIONS = {".pdf", ".jpg", ".png", ".gif", ".svg"};

    public void sendUrlToKafka(String url) {
        try {
            if (!isValidUrl(url)) {
                logger.error("Error crawling URL : " + url);
                return;
            }
            kafkaProducerService.sendUrl(url);
        } catch (Exception e) {
            logger.error("Error crawling url {} : {}", url, e.getMessage(), e);
        }
    }

    public void sendUrlListToKafka(List<String> urlList) {
        try {
            for (String url : urlList) {
                if (!isValidUrl(url)) {
                    logger.error("Error crawling URL : " + url);
                    return;
                }
                kafkaProducerService.sendUrl(url);
            }
        } catch (Exception e) {
            logger.error("Error crawling url", e.getMessage(), e);
        }
    }
    private boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            logger.error("The URL is empty or null : {}", url);
            return false;
        }
        if (!url.startsWith("http")) {
            logger.error("The URL don't statt with HTTP : {}", url);
            return false;
        }
        for (String extension : INVALID_EXTENSIONS) {
            if (url.endsWith(extension)) {
                logger.error("The URL contains an unauthorized extension : {}", url);
                return false;
            }
        }
        return true;
    }
}







