package com.mycompany.authenticationservices.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class RolePermissionRequestDTO {
    private Long roleId;
    private List<Long> permissionIds;
}
