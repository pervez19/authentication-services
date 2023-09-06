package com.mycompany.authenticationservices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PermissionDTO {
    private Long id;
    private String name;
    private String value;
    private String description;
    private String url;
}
