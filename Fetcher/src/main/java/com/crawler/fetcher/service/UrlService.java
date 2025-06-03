package com.crawler.fetcher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Service to validate and check permissions for URLs based on robots.txt rules
 */
@Service
public class UrlService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private static final String[] INVALID_EXTENSIONS = { ".pdf", ".jpg", ".png", ".gif", ".svg" };

    /**
     * Checks if a URL is allowed to be crawled based on robots.txt rules
     * 
     * @param url       The URL to check
     * @param userAgent The user agent to check rules for
     * @param rulesMap  Map of rules from robots.txt
     * @return true if URL is allowed, false if disallowed
     */
    public boolean isUrlAllowedForScrapping(String url, String userAgent,
            Map<String, Map<String, List<String>>> rulesMap) {
        // Check if map contains rules for specified agent
        Map<String, List<String>> agentRules = rulesMap.get(userAgent);

        // If no rules defined for this agent, check global rules ("*")
        if (agentRules == null) {
            agentRules = rulesMap.get("*");
        }

        // If rules found for agent or global rules
        if (agentRules != null) {
            // Check if URL is in "Disallow" rules
            List<String> disallowedUrls = agentRules.get("Disallow");
            if (disallowedUrls != null) {
                for (String disallowedUrl : disallowedUrls) {
                    // If URL matches a "Disallow" rule, return false
                    if (url.startsWith(disallowedUrl)) {
                        return false;
                    }
                }
            }
        }

        // Return true if URL is not in "Disallow" list
        return true;
    }

    /**
     * Validates if a URL is in correct format and has valid extension
     * 
     * @param url The URL to validate
     * @return true if URL is valid, false otherwise
     */
    public boolean isValidUrl(String url) {
        if (url == null || url.isEmpty())
            return false;

        // Check for anchors
        if (url.contains("#"))
            return false;

        // Normalize to handle parameters
        String normalizedUrl = normalizeUrl(url);

        // Check invalid extensions
        for (String extension : INVALID_EXTENSIONS) {
            if (normalizedUrl.endsWith(extension))
                return false;
        }
        return normalizedUrl.startsWith("http");
    }

    /**
     * Normalizes a URL by removing query parameters and fragments
     * 
     * @param url The URL to normalize
     * @return The normalized URL
     */
    private String normalizeUrl(String url) {
        try {
            URI uri = new URI(url);
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null).toString();
        } catch (URISyntaxException e) {
            logger.warn("Invalid URL: {}", url);
            return url;
        }
    }
}
