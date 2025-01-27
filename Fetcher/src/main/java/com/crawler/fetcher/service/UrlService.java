package com.crawler.fetcher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@Service
public class UrlService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private static final String[] INVALID_EXTENSIONS = {".pdf", ".jpg", ".png", ".gif", ".svg"};

    public boolean isUrlAllowedForScrapping(String url, String userAgent, Map<String, Map<String, List<String>>> rulesMap) {
        // Vérifie si la map contient des règles pour l'agent spécifié
        Map<String, List<String>> agentRules = rulesMap.get(userAgent);

        // Si aucune règle n'est définie pour cet agent, on vérifie les règles globales ("*")
        if (agentRules == null) {
            agentRules = rulesMap.get("*");
        }

        // Si des règles sont trouvées pour l'agent ou les règles globales
        if (agentRules != null) {
            // Vérifie si l'URL est dans les règles "Disallow"
            List<String> disallowedUrls = agentRules.get("Disallow");
            if (disallowedUrls != null) {
                for (String disallowedUrl : disallowedUrls) {
                    // Si l'URL correspond à une règle "Disallow", on retourne false
                    if (url.startsWith(disallowedUrl)) {
                        return false;
                    }
                }
            }
        }

        // Retourne true si l'URL n'est pas dans la liste "Disallow"
        return true;
    }

    public boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) return false;

        // Vérifier les ancres
        if (url.contains("#")) return false;

        // Normalisation pour gérer les paramètres
        String normalizedUrl = normalizeUrl(url);

        // Vérifier les extensions invalides
        for (String extension : INVALID_EXTENSIONS) {
            if (normalizedUrl.endsWith(extension)) return false;
        }
        return normalizedUrl.startsWith("http");
    }

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
