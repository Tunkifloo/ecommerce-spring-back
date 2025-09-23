package com.springback.ecommerce_layers.dto.response;

import com.springback.ecommerce_layers.entity.Product;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Double price,
        Integer stock) {

    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock());
    }
}