package com.crawler.fetcher.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC_FETCH = "crawler-urls";
    private static final String TOPIC_PARSE = "urls-to-parse";

    public void sendUrlToFetch(String url) {
        kafkaTemplate.send(TOPIC_FETCH, url);
        logger.info("Url correctly sent to Kafka for fetching : {}", url);
    }
}
