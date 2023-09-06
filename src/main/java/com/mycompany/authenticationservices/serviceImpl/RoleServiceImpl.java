package com.mycompany.authenticationservices.serviceImpl;


import com.mycompany.authenticationservices.dto.PermissionDTO;
import com.mycompany.authenticationservices.dto.RoleDTO;
import com.mycompany.authenticationservices.dto.RolePermissionRequestDTO;
import com.mycompany.authenticationservices.dto.RolePermissionResponseDTO;
import com.mycompany.authenticationservices.entity.RoleEntity;
import com.mycompany.authenticationservices.enums.RoleEnum;
import com.mycompany.authenticationservices.exception.DataNotFoundException;
import com.mycompany.authenticationservices.exception.DuplicateException;
import com.mycompany.authenticationservices.mapper.AppMapper;
import com.mycompany.authenticationservices.repository.RoleRepository;
import com.mycompany.authenticationservices.service.PermissionService;
import com.mycompany.authenticationservices.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final PermissionService permissionService;

    @Override
    public RoleEntity getByName(RoleEnum roleEnum) {
        return roleRepository.findByCode(roleEnum)
                .orElseThrow(() -> new DataNotFoundException("RoleEntity not found"));
    }

    @Override
    public RoleDTO save(RoleDTO roleDTO) {
        if (roleRepository.findByCode(roleDTO.getCode()).isPresent()) {
            throw new DuplicateException("Role Already exist !!");
        }
        return AppMapper.roleEntityToDTO(roleRepository.save(AppMapper.roleDtoToEntity(roleDTO)));
    }

    @Override
    public RoleDTO update(RoleDTO newroleDTO) {
        checkUpdatedRoleIsExist(newroleDTO);
        return AppMapper.roleEntityToDTO(roleRepository.findById(newroleDTO.getId()).map(role -> {
            if (!ObjectUtils.isEmpty(StringUtils.trimToNull(newroleDTO.getName()))) {
                role.setName(newroleDTO.getName().trim());
            }
            if (!ObjectUtils.isEmpty(StringUtils.trimToNull(newroleDTO.getDescription()))) {
                role.setDescription(newroleDTO.getDescription().trim());
            }
            if (!ObjectUtils.isEmpty(StringUtils.trimToNull(newroleDTO.getWelcomePageURL()))) {
                role.setWelcomePageURL(newroleDTO.getWelcomePageURL().trim());
            }
            return roleRepository.save(role);
        }).orElseThrow(() -> new DataNotFoundException("Role not found for this id" + newroleDTO.getId())));
    }

    @Override
    public void delete(Long roleId) {
        roleRepository.findById(roleId).orElseThrow(() -> new DataNotFoundException("Role not found for this id" + roleId));
        roleRepository.deleteById(roleId);
    }

    @Override
    public RoleDTO findById(Long id) {
        return AppMapper.roleEntityToDTO(roleRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Role not found for this id" + id)));
    }

    @Override
    public List<RoleDTO> findAll() {
        return roleRepository.findAll().stream().map(role -> AppMapper.roleEntityToDTO(role)).collect(Collectors.toList());
    }

    private void checkUpdatedRoleIsExist(RoleDTO roleDTO) {
        if (roleRepository.findByCodeAndIdNot(roleDTO.getCode(), roleDTO.getId()).isPresent()) {
            throw new DuplicateException("Role Already exist !!");
        }
    }

    @Override
    public RolePermissionResponseDTO updateRolePermission(RolePermissionRequestDTO rolePermissionRequestDTO) {
        return AppMapper.roleEntityToRolePermissionResponseDTO(roleRepository.findById(rolePermissionRequestDTO.getRoleId()).map(role -> {
            role.getPermissions().addAll(
                    rolePermissionRequestDTO.getPermissionIds().stream().distinct().map(id -> permissionService.getPermissionById(id)).collect(Collectors.toSet()));
            return roleRepository.save(role);
        }).orElseThrow(() -> new DataNotFoundException("Role not found for this id" + rolePermissionRequestDTO.getRoleId())));
    }

    @Override
    public List<PermissionDTO> getPermissionByRoleIds(Set<Long> roleIds) {
        return roleRepository.findAllById(roleIds).stream().flatMap(roleEntity -> roleEntity.getPermissions().stream()).map(permissionEntity->AppMapper.permissionEntityToDTO(permissionEntity)).collect(Collectors.toList());
    }

}
