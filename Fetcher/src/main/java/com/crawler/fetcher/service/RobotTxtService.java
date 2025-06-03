package com.crawler.fetcher.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to parse the robots.txt file of a given site and extract rules for
 * user-agents.
 */
@Service
@RequiredArgsConstructor
public class RobotTxtService {

    // Logger for logging errors and information
    private static final Logger logger = LoggerFactory.getLogger(RobotTxtService.class);

    // Injected HTTP client for making requests
    private final HttpClient httpClient;

    /**
     * Parses the robots.txt file of a given site and extracts rules for
     * user-agents.
     * Only rules for 'User-agent: *' are considered.
     *
     * @param siteUrl The base URL of the site (e.g., "https://example.com")
     * @return A map where the key is the user-agent, and the value is a map of rule
     *         types ("Allow", "Disallow") to lists of paths.
     */
    public Map<String, Map<String, List<String>>> parseRobotsTxt(String siteUrl) {
        Map<String, Map<String, List<String>>> rulesMap = new HashMap<>();
        Map<String, List<String>> currentAgentRules = null;

        try {
            // Build the URI for the robots.txt file
            URI robotsTxtUri = URI.create(siteUrl + "/robots.txt");

            // Create a GET request with gzip encoding
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(robotsTxtUri)
                    .header("Accept-Encoding", "gzip")
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the request was successful
            if (response.statusCode() == 200) {
                // Split the content into lines
                String[] lines = response.body().split("\n");
                for (String line : lines) {
                    line = line.trim();

                    // Detect user-agent section
                    if (line.startsWith("User-agent:")) {
                        String agent = line.substring(11).trim();
                        if ("*".equals(agent)) {
                            // Only process rules for the generic user-agent
                            currentAgentRules = rulesMap.computeIfAbsent(agent, k -> new HashMap<>());
                        } else {
                            // Ignore rules for other user-agents
                            currentAgentRules = null;
                        }
                    }

                    // If in a relevant user-agent section, process rules
                    if (currentAgentRules != null) {
                        if (line.startsWith("Disallow:")) {
                            String disallowUrl = line.substring(9).trim();
                            currentAgentRules.computeIfAbsent("Disallow", k -> new ArrayList<>()).add(disallowUrl);
                        } else if (line.startsWith("Allow:")) {
                            String allowUrl = line.substring(6).trim();
                            currentAgentRules.computeIfAbsent("Allow", k -> new ArrayList<>()).add(allowUrl);
                        }
                    }
                }
            } else {
                logger.error("Failed to fetch robots.txt. HTTP code: {}", response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Error reading robots.txt: {}", e.getMessage());
        }

        return rulesMap;
    }

}
