package com.mycompany.authenticationservices.service;



import com.mycompany.authenticationservices.dto.PermissionDTO;
import com.mycompany.authenticationservices.dto.RoleDTO;
import com.mycompany.authenticationservices.dto.RolePermissionRequestDTO;
import com.mycompany.authenticationservices.dto.RolePermissionResponseDTO;
import com.mycompany.authenticationservices.entity.RoleEntity;
import com.mycompany.authenticationservices.enums.RoleEnum;

import java.util.List;
import java.util.Set;

public interface RoleService {
    RoleEntity getByName(RoleEnum roleEnum);

    RoleDTO save(RoleDTO roleDTO);

    RoleDTO update(RoleDTO newroleDTO);

    void delete(Long roleId);

    RoleDTO findById(Long id);

    List<RoleDTO> findAll();

    RolePermissionResponseDTO updateRolePermission(RolePermissionRequestDTO rolePermissionRequestDTO);

    List<PermissionDTO> getPermissionByRoleIds(Set<Long> roleIds);
}
