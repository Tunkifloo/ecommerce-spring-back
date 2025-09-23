package com.springback.ecommerce_layers.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductUpdateRequest(

        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre no puede exceder 100 caracteres") String name,
        @NotBlank(message = "La descripcion es obligatorio") @Size(max = 100, message = "La descripcion no puede exceder 100 caracteres") String description,
        Double price,
        Integer stock) {
}
