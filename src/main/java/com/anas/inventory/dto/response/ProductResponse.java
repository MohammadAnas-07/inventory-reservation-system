package com.anas.inventory.dto.response;

import com.anas.inventory.entity.Product;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse (
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProductResponse fromEntity(Product product){
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
