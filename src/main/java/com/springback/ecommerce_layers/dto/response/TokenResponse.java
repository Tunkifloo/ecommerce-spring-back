package com.springback.ecommerce_layers.dto.response;

public record TokenResponse(
        String token,
        boolean firstLogin,
        String username,
        String role
) {}