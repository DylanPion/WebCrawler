package com.crawler.fetcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service to check if a page has already been crawled using Redis cache.
 */
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Boolean> redisTemplate;
    private static final String REDIS_PREFIX = "crawled:"; // Prefix to avoid key collisions in Redis
    private static final long CACHE_EXPIRATION_SECONDS = 3600;

    // Check if page has already been crawled using Redis cache
    public boolean hasBeenCrawledRecently(String url) {
        String redisKey = REDIS_PREFIX + url;
        Boolean isCrawled = redisTemplate.opsForValue().get(redisKey);

        if (Boolean.TRUE.equals(isCrawled)) {
            return true; // URL already crawled
        }

        // Add to cache if not present
        redisTemplate.opsForValue().set(redisKey, true, CACHE_EXPIRATION_SECONDS, TimeUnit.SECONDS);
        return false;
    }
}
