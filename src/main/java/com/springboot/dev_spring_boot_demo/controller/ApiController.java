package com.springboot.dev_spring_boot_demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/get-models")
    public ResponseEntity<?> getModels(@RequestParam("url") String baseUrl) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("success", false);
            errorBody.put("error", "URL parameter is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
        }

        // Append /v1/models if not already present.
        String url;
        if (!baseUrl.contains("/v1/models")) {
            // Remove trailing slashes.
            baseUrl = baseUrl.replaceAll("/+$", "");
            url = baseUrl + "/v1/models";
        } else {
            url = baseUrl;
        }

        try {
            WebClient webClient = WebClient.create();
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(5));

            List<String> models = new ArrayList<>();
            if (response != null && response.containsKey("data")) {
                Object dataObj = response.get("data");
                if (dataObj instanceof List) {
                    List<?> dataList = (List<?>) dataObj;
                    for (Object itemObj : dataList) {
                        if (itemObj instanceof Map) {
                            Map<?, ?> item = (Map<?, ?>) itemObj;
                            if ("model".equals(item.get("object")) && item.containsKey("id")) {
                                models.add(String.valueOf(item.get("id")));
                            }
                        }
                    }
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", models);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("success", false);
            errorBody.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }

    @GetMapping("/test-gemini")
    public ResponseEntity<Map<String, Object>> testGeminiAPI(@RequestParam("api_key") String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("success", false);
            errorBody.put("error", "API key is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
        }

        // Construct the Gemini API test URL.
        String encodedApiKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + encodedApiKey;

        // Prepare the payload.
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", "Hello World!");
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));
        payload.put("contents", List.of(content));

        try {
            WebClient webClient = WebClient.builder().build();
            Map<String, Object> response = webClient.post()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(5));

            if (response != null && response.containsKey("candidates")) {
                List<?> candidates = (List<?>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Object candidateObj = candidates.get(0);
                    if (candidateObj instanceof Map) {
                        Map<?, ?> candidate = (Map<?, ?>) candidateObj;
                        if (candidate.containsKey("content")) {
                            Object contentObj = candidate.get("content");
                            if (contentObj instanceof Map) {
                                Map<?, ?> contentMap = (Map<?, ?>) contentObj;
                                if (contentMap.containsKey("parts")) {
                                    List<?> parts = (List<?>) contentMap.get("parts");
                                    if (!parts.isEmpty()) {
                                        return ResponseEntity.ok(Map.of("success", true));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return ResponseEntity.ok(Map.of("success", false));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", ex.getMessage()));
        }
    }
}
