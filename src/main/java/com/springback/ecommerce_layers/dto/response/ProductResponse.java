package com.springback.ecommerce_layers.dto.response;

import com.springback.ecommerce_layers.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Boolean active,
        Boolean available,
        SellerInfo seller,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getActive(),
                product.isAvailable(),
                SellerInfo.fromUser(product.getSeller()),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public record SellerInfo(
            Long id,
            String fullName,
            String email
    ) {
        public static SellerInfo fromUser(com.springback.ecommerce_layers.entity.User user) {
            return new SellerInfo(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail()
            );
        }
    }
}