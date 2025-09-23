package com.springback.ecommerce_layers.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductUpdateRequest(
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String name,

        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        String description,

        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        @DecimalMax(value = "999999.99", message = "El precio no puede exceder 999,999.99")
        BigDecimal price,

        @Min(value = 0, message = "El stock no puede ser negativo")
        @Max(value = 999999, message = "El stock no puede exceder 999,999")
        Integer stock,

        Boolean active
) {}