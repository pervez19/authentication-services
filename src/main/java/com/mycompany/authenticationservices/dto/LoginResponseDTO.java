package com.mycompany.authenticationservices.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class LoginResponseDTO {
    private String uuid;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String token;
    private Integer tokenDuration;
    private List roles;
    private List permissions;
}
