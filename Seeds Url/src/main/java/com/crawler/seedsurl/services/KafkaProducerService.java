package com.crawler.seedsurl.services;

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

    private static final String TOPIC = "crawler-urls";

    public void sendUrl(String url) {
        kafkaTemplate.send(TOPIC, url);
        logger.info("Url correctly sent to Kafka : {}", url);
    }
}
