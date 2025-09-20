package com.springback.ecommerce_layers.dto.request;

import com.springback.ecommerce_layers.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String firstName,

        @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
        String lastName,

        @Email(message = "El email debe tener un formato válido")
        @Size(max = 150, message = "El email no puede exceder 150 caracteres")
        String email,

        @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
        String phone,

        User.UserRole role,

        Boolean active
) {}