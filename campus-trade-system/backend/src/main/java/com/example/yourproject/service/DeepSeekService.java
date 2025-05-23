package com.example.yourproject.service;

public interface DeepSeekService {
    /**
     * Generates a product description based on the product title.
     * @param productTitle The title of the product.
     * @return A generated description string, or null/empty if generation fails.
     */
    String generateDescription(String productTitle);
}