package com.crawler.fetcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Boolean> redisTemplate;
    private static final String REDIS_PREFIX = "crawled:"; // Préfixe pour éviter les collisions de clés dans Redis
    private static final long CACHE_EXPIRATION_SECONDS = 3600;


    // Vérification de la page déjà crawlé avec Ehcache
    public boolean hasBeenCrawledRecently(String url) {
        String redisKey = REDIS_PREFIX + url;
        Boolean isCrawled = redisTemplate.opsForValue().get(redisKey);

        if (Boolean.TRUE.equals(isCrawled)) {
            return true; // URL déjà crawlée
        }

        // Ajouter au cache si non présent
        redisTemplate.opsForValue().set(redisKey, true, CACHE_EXPIRATION_SECONDS, TimeUnit.SECONDS);
        return false;
    }
}
