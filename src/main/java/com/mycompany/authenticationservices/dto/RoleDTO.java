package com.mycompany.authenticationservices.dto;


import com.mycompany.authenticationservices.enums.RoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class RoleDTO {
    private long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private RoleEnum code;
    private String description;
    private String welcomePageURL;
}
