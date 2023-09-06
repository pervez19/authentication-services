package com.mycompany.authenticationservices.service;



import com.mycompany.authenticationservices.dto.PermissionDTO;
import com.mycompany.authenticationservices.entity.PermissionEntity;

import java.util.List;
import java.util.Set;

public interface PermissionService {
    PermissionEntity getPermissionById(Long id);

    PermissionDTO save(PermissionDTO permissionDTO);

    List<PermissionDTO> getPermissions();

    PermissionDTO update(PermissionDTO permissionDTO);

    void delete(Long id);
}
