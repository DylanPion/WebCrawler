package com.crawler.fetcher.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.crawler.fetcher.dto.WebPageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service responsible for producing messages to Kafka topics.
 * Handles sending URLs for crawling and parsing.
 */
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    // Logger for this class
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    // Kafka template for sending messages
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Kafka topic names
    private static final String TOPIC_FETCH = "crawlers-url"; // Topic for URLs to be crawled
    private static final String TOPIC_PARSE = "urls-to-parse"; // Topic for URLs to be parsed

    // ObjectMapper for serialization
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends a URL to the crawler topic for fetching.
     * 
     * @param url The URL to be crawled
     */
    public void sendUrlToFetch(String url) {
        kafkaTemplate.send(TOPIC_FETCH, url);
        logger.info("URL successfully sent to Kafka for fetching: {}", url);
    }

    /**
     * Sends a WebPageDTO to the parser topic for parsing.
     * 
     * @param webPageDTO The WebPageDTO to be parsed
     */
    public void sendUrlToParse(WebPageDTO webPageDTO) {
        try {
            String json = objectMapper.writeValueAsString(webPageDTO);
            kafkaTemplate.send(TOPIC_PARSE, json);
            logger.info("WebPageDTO successfully sent to Kafka for parsing");
        } catch (Exception e) {
            logger.error("Failed to serialize WebPageDTO: {}", e.getMessage());
        }
    }

    /**
     * Sends a file path to the parser topic for parsing.
     * 
     * @param filePath The file path to be parsed
     */
    public void sendFilePathToParse(String filePath) {
        kafkaTemplate.send(TOPIC_PARSE, filePath);
        logger.info("File path sent to Kafka for parsing: {}", filePath);
    }
}
