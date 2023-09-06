package com.mycompany.authenticationservices.dto;

import com.mycompany.authenticationservices.enums.RoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor
public class RolePermissionResponseDTO extends RoleDTO{
    private List<PermissionDTO> permissions;
}
