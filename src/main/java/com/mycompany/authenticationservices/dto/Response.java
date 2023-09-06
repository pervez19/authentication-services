package com.mycompany.authenticationservices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response<R> {
    private String status;
    private String message;
    private R data;
}
