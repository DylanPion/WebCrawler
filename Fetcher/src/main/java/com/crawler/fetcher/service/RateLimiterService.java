package com.crawler.fetcher.service;

import org.springframework.stereotype.Service;

/**
 * Service to limit the number of requests per second
 * to avoid overloading target servers.
 */
@Service
public class RateLimiterService {
    /**
     * Minimum delay between two requests in milliseconds (1 second)
     */
    private static final long REQUEST_DELAY_MS = 1000;

    /**
     * Timestamp of the last request made
     */
    private long lastRequestTime = 0;

    /**
     * Limits the number of requests by enforcing a minimum delay between each
     * request.
     * If a request comes too early, the method pauses the thread to respect the
     * delay.
     *
     * @throws InterruptedException if the thread is interrupted during the pause
     */
    public void throttleRequests() throws InterruptedException {
        long now = System.currentTimeMillis();
        if (now - lastRequestTime < REQUEST_DELAY_MS) {
            Thread.sleep(REQUEST_DELAY_MS - (now - lastRequestTime));
        }
        lastRequestTime = now;
    }
}
