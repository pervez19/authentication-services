package com.mycompany.authenticationservices.dto;


import com.mycompany.authenticationservices.enums.RoleEnum;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;


@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class UserDTO {
    private Long id;
    @NotBlank(message = "name is mandatory")
    @Size(min = 2, max = 100)
    private String name;

    private String accountId;
    @Email
    @NotNull
    @NotBlank(message = "email is mandatory")
    private String email;
    private List<RoleEnum> roles;
}


