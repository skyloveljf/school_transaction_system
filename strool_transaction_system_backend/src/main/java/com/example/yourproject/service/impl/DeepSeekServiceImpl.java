package com.example.yourproject.service.impl;

import com.example.yourproject.dto.deepseek.DeepSeekChatRequest;
import com.example.yourproject.dto.deepseek.DeepSeekChatResponse;
import com.example.yourproject.service.DeepSeekService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekServiceImpl.class);

    private final RestTemplate restTemplate;

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.baseurl}")
    private String baseUrl;
    
    @Value("${deepseek.api.model}")
    private String model;

    public DeepSeekServiceImpl() {
        this.restTemplate = new RestTemplate(); // Consider configuring RestTemplate as a Bean for more complex scenarios
    }

    @Override
    public String generateDescription(String productTitle) {
        if (productTitle == null || productTitle.trim().isEmpty()) {
            logger.warn("Product title is empty, cannot generate description.");
            return null;
        }

        String apiUrl = baseUrl + "/chat/completions"; // Endpoint for chat completions

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // Simple prompt, you can customize this further
        String systemPrompt = "You are a helpful assistant that writes concise and appealing product descriptions for an online marketplace.";
        String userPrompt = "Generate a short product description for a product titled: \"" + productTitle + "\". Focus on key features and benefits if inferable.请用中文生成，不要超过300字";
        
        DeepSeekChatRequest requestPayload = new DeepSeekChatRequest(model, systemPrompt, userPrompt);

        HttpEntity<DeepSeekChatRequest> entity = new HttpEntity<>(requestPayload, headers);

        try {
            logger.info("Calling DeepSeek API. URL: {}, Model: {}", apiUrl, model);
            ResponseEntity<DeepSeekChatResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    DeepSeekChatResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                DeepSeekChatResponse chatResponse = response.getBody();
                if (chatResponse.getChoices() != null && !chatResponse.getChoices().isEmpty()) {
                    String description = chatResponse.getChoices().get(0).getMessage().getContent();
                    logger.info("Successfully generated description for title '{}': {}", productTitle, description);
                    return description.trim();
                } else {
                    logger.warn("DeepSeek API response does not contain choices for title: {}", productTitle);
                }
            } else {
                logger.error("Error calling DeepSeek API: Status Code {}, Body: {}", response.getStatusCode(), response.getBody());
            }
        } catch (HttpClientErrorException e) {
            logger.error("HttpClientErrorException while calling DeepSeek API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Exception while calling DeepSeek API for title '{}'", productTitle, e);
        }
        return null; // Or throw an exception if preferred
    }
}