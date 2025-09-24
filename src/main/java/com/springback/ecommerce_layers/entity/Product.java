package com.springback.ecommerce_layers.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean active = true;

    @Lob
    @Column(name = "image_data", columnDefinition = "TEXT")
    private String imageData;

    @Column(name = "image_content_type", length = 100)
    private String imageContentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (active == null) {
            active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isAvailable() {
        return active && stock > 0;
    }

    public void reduceStock(Integer quantity) {
        if (quantity > stock) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        this.stock -= quantity;
    }

    public void addStock(Integer quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        this.stock += quantity;
    }

    public boolean hasImage() {
        return imageData != null && !imageData.trim().isEmpty();
    }

    public String getImageDataUrl() {
        if (!hasImage()) {
            return null;
        }
        return "data:" + (imageContentType != null ? imageContentType : "image/jpeg") + ";base64," + imageData;
    }
}