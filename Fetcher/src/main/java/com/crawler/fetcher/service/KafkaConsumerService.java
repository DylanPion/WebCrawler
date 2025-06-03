package com.crawler.fetcher.service;

import com.crawler.fetcher.component.ThreadPoolManager;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service responsible for consuming messages from Kafka topics and managing the
 * thread pool
 * for crawling operations.
 */
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final CrawlerService crawlerService;
    private final ThreadPoolManager threadPoolManager;

    // Check thread pool size and adjust if necessary
    @Scheduled(fixedRate = 10000) // Every 10 seconds
    public void monitorThreadPool() {
        threadPoolManager.monitorThreadPool();
    }

    /**
     * Listens to the crawler-urls topic and processes incoming URLs for crawling
     * using a thread pool executor.
     *
     * @param url The URL to be crawled
     */
    @KafkaListener(topics = "crawlers-url", groupId = "crawler-group")
    public void crawlUrl(String url) {
        threadPoolManager.getThreadPoolExecutor().submit(() -> {
            try {
                crawlerService.startCrawling(url);
            } catch (Exception e) {
                logger.error("Error processing URL {}: {}", url, e.getMessage(), e);
            }
        });
    }

    // Shutdown the thread pool when application closes
    @PreDestroy
    public void shutdownExecutor() {
        threadPoolManager.shutdown();
    }
}
