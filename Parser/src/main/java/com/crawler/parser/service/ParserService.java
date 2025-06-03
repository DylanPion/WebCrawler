package com.crawler.parser.service;

import com.crawler.parser.dto.WebPageDTO;
import com.crawler.parser.entities.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.crawler.parser.repositories.WebPageRepositoy;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
public class ParserService {

    private static final Logger logger = LoggerFactory.getLogger(ParserService.class);

    private final ObjectMapper objectMapper;
    private final WebPageRepositoy webPageRepository;

    public ParserService(ObjectMapper objectMapper, WebPageRepositoy webPageRepository) {
        this.objectMapper = objectMapper;
        this.webPageRepository = webPageRepository;
    }

    @KafkaListener(topics = "urls-to-parse", groupId = "parser-group")
    public void parse(String message) {
        try {
            WebPageDTO webPageDTO = objectMapper.readValue(message, WebPageDTO.class);
            logger.info("Parsing WebPageDTO: {}", webPageDTO);
            WebPage webPage = new WebPage();
            webPage.setUrl(webPageDTO.getUrl());
            webPage.setTitle(webPageDTO.getTitle());

            String fullText = checkTextSize(webPageDTO.getText());

            webPage.setText(fullText);
            webPage.setMetaData(webPageDTO.getMetaData());
            webPage.setCrawlDate(LocalDateTime.now());
            webPageRepository.save(webPage);
            logger.info("WebPage saved: {}", webPage);
        } catch (Exception e) {
            logger.error("Failed to parse WebPageDTO: {}", e.getMessage());
        }
    }

    public String checkTextSize(String fullText) {
        // Exemple très simple pour montrer un stockage de meta datz
        String limitedText = fullText.length() > 5000 ? fullText.substring(0, 100) : fullText;
        return limitedText;
    }
}

