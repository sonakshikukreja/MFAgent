package in.ai.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @PostMapping("/query")
    public Map<String, Object> query(@RequestBody Map<String, String> body) {
        String userPrompt = body.get("prompt");
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        try {
            Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
            StringBuilder contextBuilder = new StringBuilder();

            // 1. Add content from specific NAV text files first
            List<String> specificUrls = List.of(
                "https://portal.amfiindia.com/spages/NAVAll.txt",
                "https://portal.amfiindia.com/spages/NAVOpen.txt",
                "https://portal.amfiindia.com/spages/NAVClose.txt",
                "https://portal.amfiindia.com/spages/NAVInterval.txt"
            );

            // Create an HttpClient that trusts all certificates
            HttpClient client = HttpClient.newBuilder()
                .sslContext(SSLCertificateValidation.getInsecureSslContext())
                .build();

            for (String url : specificUrls) {
                try {
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String content = response.body();
                    
                    contextBuilder.append("Source: ").append(url).append("\n");
                    contextBuilder.append("Content: ").append(content).append("\n\n");
                    visitedUrls.add(url); // Mark as visited
                } catch (IOException | InterruptedException e) {
                    System.err.println("Failed to fetch specific URL " + url + ": " + e.getMessage());
                }
            }
            
            // 2. Start recursive scraping from the main site
            scrapeRecursive(baseUrl, 0, visitedUrls, contextBuilder);

            String context = contextBuilder.toString();
            
            if (context.length() > 30000) {
                context = context.substring(0, 30000) + "\n...(truncated due to size limit)...";
            }

            String fullPrompt = "You are an AI assistant specialized in Indian Mutual Funds. " +
                    "Strictly answer the query using ONLY the following context scraped from the official AMFI website (www.amfiindia.com). " +
                    "If the answer is not in the context, state that you cannot find the information on the AMFI website. " +
                    "Do not use outside knowledge. " +
                    "Format your response using Markdown. Use tables for presenting numerical data or comparisons, and bullet points for lists.\n\n" +
                    "Context:\n" + context + "\n\nUser Query: " + userPrompt;

            // Call Gemini API
            String llmResponse = callGemini(fullPrompt);
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

            String text = doc.body().text().replaceAll("\\s+", " ").trim();
            
            synchronized (contextBuilder) {
                contextBuilder.append("Source: ").append(url).append("\n");
                contextBuilder.append("Content: ").append(text).append("\n\n");
            }

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.attr("abs:href");
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

    private String callGemini(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            String fullUrl = geminiApiUrl + "?key=" + geminiApiKey;

            // Construct the request body in the format Gemini expects
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", Collections.singletonList(part));

            Map<String, Object> payload = new HashMap<>();
            payload.put("contents", Collections.singletonList(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, entity, String.class);

            // Parse the response to extract the text
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode textNode = root.at("/candidates/0/content/parts/0/text");
            
            if (textNode.isMissingNode()) {
                // Handle cases where the response might be a safety block
                JsonNode blockReason = root.at("/promptFeedback/blockReason");
                if (!blockReason.isMissingNode()) {
                    return "Error: The request was blocked by Gemini for the following reason: " + blockReason.asText();
                }
                return "Error: Could not extract a valid response from Gemini.";
            }

            return textNode.asText();

        } catch (Exception e) {
            return "Error calling Gemini API: " + e.getMessage();
        }
    }
}