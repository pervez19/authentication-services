package com.mycompany.authenticationservices.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDTO(@NotBlank String refreshToken) {}
