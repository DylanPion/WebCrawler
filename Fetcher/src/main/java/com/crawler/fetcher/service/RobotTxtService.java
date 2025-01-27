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

@Service
@RequiredArgsConstructor
public class RobotTxtService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final HttpClient httpClient;

    public Map<String, Map<String, List<String>>> parseRobotsTxt(String siteUrl) {
        Map<String, Map<String, List<String>>> rulesMap = new HashMap<>();
        Map<String, List<String>> currentAgentRules = null;

        try {
            // Construire l'URI pour accéder au fichier robots.txt
            URI robotsTxtUri = URI.create(siteUrl + "/robots.txt");

            // Construire une requête GET
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(robotsTxtUri)
                    .header("Accept-Encoding", "gzip") //Active la compression GZIP dans les requête pour réduire la bande passante.
                    .GET()
                    .build();

            // Envoyer la requête et obtenir la réponse
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Vérifier le code de réponse HTTP
            if (response.statusCode() == 200) {
                // Lire le contenu du fichier robots.txt
                String[] lines = response.body().split("\n");
                for (String line : lines) {
                    line = line.trim(); // Supprime les espaces inutiles

                    if (line.startsWith("User-agent:")) {
                        String agent = line.substring(11).trim();
                        if ("*".equals(agent)) {
                            // Si le User-agent correspond, on crée une nouvelle map pour cet agent
                            currentAgentRules = rulesMap.computeIfAbsent(agent, k -> new HashMap<>());
                        } else {
                            // Si l'agent ne correspond pas, on ignore les règles suivantes jusqu'à la prochaine section
                            currentAgentRules = null;
                        }
                    }

                    if (currentAgentRules != null) {
                        if (line.startsWith("Disallow:")) {
                            String disallowUrl = line.substring(9).trim(); // Extraire l'URL après "Disallow:"
                            currentAgentRules.computeIfAbsent("Disallow", k -> new ArrayList<>()).add(disallowUrl);
                        } else if (line.startsWith("Allow:")) {
                            String allowUrl = line.substring(6).trim(); // Extraire l'URL après "Allow:"
                            currentAgentRules.computeIfAbsent("Allow", k -> new ArrayList<>()).add(allowUrl);
                        }
                    }
                }
            } else {
                System.err.println("Impossible de récupérer le fichier robots.txt. Code HTTP : " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du fichier robots.txt : " + e.getMessage());
            logger.error("Erreur lors de la lecture du fichier robots.txt : {}", e.getMessage());
        }

        return rulesMap;
    }

}
