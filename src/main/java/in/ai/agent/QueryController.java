package in.ai.agent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
public class QueryController {

    @Value("${scraper.base-url}")
    private String baseUrl;

    @Value("${scraper.max-depth}")
    private int maxDepth;

    @Value("${scraper.max-pages}")
    private int maxPages;

    @PostMapping("/query")
    public Map<String, Object> query(@RequestBody Map<String, String> body) {
        String userPrompt = body.get("prompt");
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            // Recursively fetch context from AMFI website
            Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
            StringBuilder contextBuilder = new StringBuilder();
            
            // Start scraping from the home page
            scrapeRecursive(baseUrl, 0, visitedUrls, contextBuilder);

            String context = contextBuilder.toString();
            
            // Truncate context if it's too large for the LLM context window (approx 30k chars)
            if (context.length() > 30000) {
                context = context.substring(0, 30000) + "\n...(truncated due to size limit)...";
            }

            // Construct full prompt for Ollama
            String fullPrompt = "You are an AI assistant specialized in Indian Mutual Funds. " +
                    "Strictly answer the query using ONLY the following context scraped from the official AMFI website (www.amfiindia.com). " +
                    "If the answer is not in the context, state that you cannot find the information on the AMFI website. " +
                    "Do not use outside knowledge. " +
                    "Format your response using Markdown. Use tables for presenting numerical data or comparisons, and bullet points for lists.\n\n" +
                    "Context:\n" + context + "\n\nUser Query: " + userPrompt;

            // Call Ollama API
            String llmResponse = callOllama(fullPrompt);
            result.put("response", llmResponse);
            result.put("sources", String.join(", ", visitedUrls.stream().limit(5).collect(Collectors.toList())) + " (and others)");

        } catch (Exception e) {
            result.put("response", "Error processing query: " + e.getMessage());
        }

        long processingTime = System.currentTimeMillis() - startTime;
        result.put("time", processingTime + " ms");

        return result;
    }

    private void scrapeRecursive(String url, int depth, Set<String> visitedUrls, StringBuilder contextBuilder) {
        if (depth > maxDepth || visitedUrls.size() >= maxPages || visitedUrls.contains(url)) {
            return;
        }

        visitedUrls.add(url);
        System.out.println("Scraping: " + url);

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(5000)
                    .get();

            String text = doc.body().text();
            // Basic cleaning to remove excessive whitespace
            text = text.replaceAll("\\s+", " ").trim();
            
            synchronized (contextBuilder) {
                contextBuilder.append("Source: ").append(url).append("\n");
                contextBuilder.append("Content: ").append(text).append("\n\n");
            }

            // Find all links on the page
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.attr("abs:href");
                
                // Only follow links within the amfiindia.com domain and avoid non-html files (basic check)
                if (nextUrl.startsWith(baseUrl) && !nextUrl.contains("#") && !isBinaryFile(nextUrl)) {
                    scrapeRecursive(nextUrl, depth + 1, visitedUrls, contextBuilder);
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to scrape " + url + ": " + e.getMessage());
        }
    }

    private boolean isBinaryFile(String url) {
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".pdf") || lowerUrl.endsWith(".xls") || lowerUrl.endsWith(".xlsx") || 
               lowerUrl.endsWith(".zip") || lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".png");
    }

    private String callOllama(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://164.52.192.66:11434/api/generate";

            Map<String, Object> payload = new HashMap<>();
            payload.put("model", "llama3:8b"); 
            payload.put("prompt", prompt);
            payload.put("stream", false);
            // Lower temperature for more factual/deterministic answers based on context
            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.1); 
            payload.put("options", options);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() != null) {
                return (String) response.getBody().get("response");
            } else {
                return "Error calling Ollama API: Empty response";
            }
        } catch (Exception e) {
            return "Error calling Ollama API: " + e.getMessage();
        }
    }
}