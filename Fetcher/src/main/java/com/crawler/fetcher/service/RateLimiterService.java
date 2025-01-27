package com.crawler.fetcher.service;

import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {
    private static final long REQUEST_DELAY_MS = 1000; // 1 requête par seconde
    private long lastRequestTime = 0;

    // Méthode pour limiter le débit des requêtes
    public void throttleRequests() throws InterruptedException {
        long now = System.currentTimeMillis();
        if (now - lastRequestTime < REQUEST_DELAY_MS) {
            Thread.sleep(REQUEST_DELAY_MS - (now - lastRequestTime));
        }
        lastRequestTime = now;
    }
}
