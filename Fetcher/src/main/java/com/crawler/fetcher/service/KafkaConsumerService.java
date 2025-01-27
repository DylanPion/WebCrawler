package com.crawler.fetcher.service;

import com.crawler.fetcher.component.ThreadPoolManager;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final CrawlerService crawlerService;
    private final ThreadPoolManager threadPoolManager;

    // Vérification de la taille du pool et ajustement si nécessaire
    @Scheduled(fixedRate = 10000) // Toutes les 10 secondes
    public void monitorThreadPool() {
        threadPoolManager.monitorThreadPool();
    }

    @KafkaListener(topics = "crawler-urls", groupId = "crawler-group")
    public void crawlUrl(String url) {
        threadPoolManager.getThreadPoolExecutor().submit(() -> {
            try {
                crawlerService.startCrawling(url);
            } catch (Exception e) {
                logger.error("Error processing URL {}: {}", url, e.getMessage(), e);
            }
        });
    }

    // Shutdown du pool lors de la fermeture de l'application
    @PreDestroy
    public void shutdownExecutor() {
        threadPoolManager.shutdown();
    }
}
