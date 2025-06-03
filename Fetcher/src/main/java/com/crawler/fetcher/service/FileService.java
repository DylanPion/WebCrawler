package com.crawler.fetcher.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private static final String BASE_DIR = "../CrawlerFile";

    public String saveCrawledContent(String url, String content) throws IOException {
        // Encode the URL to use as folder/file name
        String encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);

        // Create the main directory if it doesn't exist
        File baseDir = new File(BASE_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        // Create the subdirectory for the URL
        File urlDir = new File(baseDir, encodedUrl);
        if (!urlDir.exists()) {
            urlDir.mkdirs();
        }

        // Create the file in the subdirectory
        File fileTxt = new File(urlDir, encodedUrl + ".txt");
        try (FileWriter writer = new FileWriter(fileTxt)) {
            writer.write(content);
        }

        // Download the HTML content using Jsoup
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(2000)
                .get();

        // Créer le fichier HTML
        File fileHtml = new File(urlDir, encodedUrl + ".html");
        try (FileWriter writer = new FileWriter(fileHtml, StandardCharsets.UTF_8)) {
            writer.write(document.outerHtml());
        } catch(Exception ex) {
            logger.warn("No HTML file for :", url, ex.getMessage());
        }

        return fileHtml.getAbsolutePath();
    }
}


