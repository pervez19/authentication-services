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
    @NotEmpty(message = "username is mandatory")
    @Size(min = 4, max = 24)
    @NotBlank(message = "username is mandatory")
    private String username;

    private String firstName;

    private String lastName;

    private String accountId;
    @Email
    @NotNull
    @NotBlank(message = "email is mandatory")
    private String email;
    private List<RoleEnum> roles;
}


