package com.crawler.seedsurl.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileReaderService {

    @Value("${file.url-to-crawl-path}")
    private String filePath;

    public List<String> readFile() throws FileNotFoundException {
        List<String> urlToCrawl = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filePath), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                urlToCrawl.add(line);
            }
            if (urlToCrawl.isEmpty()) {
                throw new IllegalArgumentException("The file is empty");
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        }catch (Exception e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }

        return urlToCrawl;
    }
}
