package com.mycompany.authenticationservices.dto;

import java.time.Instant;

public record ErrorResponse(
        String status,
        String code,
        String message,
        Instant timestamp
) {}