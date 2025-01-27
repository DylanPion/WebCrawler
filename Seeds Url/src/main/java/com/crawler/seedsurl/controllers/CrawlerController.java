package com.crawler.seedsurl.controllers;

import com.crawler.seedsurl.dto.CrawlRequestDTO;
import com.crawler.seedsurl.services.FileReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crawler.seedsurl.services.CrawlerService;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seed-urls")
public class CrawlerController {
    private final CrawlerService crawlerService;
    private final FileReaderService fileReaderService;

    @GetMapping("/start-crawling")
    public ResponseEntity<String> startCrawling() {
        crawlerService.sendUrlToKafka("https://wikipedia.org");
         return ResponseEntity.ok("URL successfully added");
    }

    @PostMapping("/send-url")
    public ResponseEntity<String> sendUrlToKafka(@RequestBody CrawlRequestDTO request) {
            crawlerService.sendUrlToKafka(request.getUrl());
            return ResponseEntity.ok("URL successfully added");
    }

    @GetMapping("/send-file-url")
    public ResponseEntity<String> readFile() throws FileNotFoundException {
            List<String> urlToCrawl = fileReaderService.readFile();
            crawlerService.sendUrlListToKafka(urlToCrawl);
            return ResponseEntity.ok("File successfully read");
    }
}
