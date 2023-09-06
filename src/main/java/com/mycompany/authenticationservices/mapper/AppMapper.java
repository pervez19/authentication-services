package com.mycompany.authenticationservices.mapper;


import com.mycompany.authenticationservices.dto.*;
import com.mycompany.authenticationservices.entity.*;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.stream.Collectors;

@UtilityClass
public class AppMapper {
    public UserDTO userEntityToDto(UserEntity userEntity) {
        var userDTO = new UserDTO();
        BeanUtils.copyProperties(userEntity, userDTO);
        userDTO.setRoles(userEntity.getRoles().stream().map(roleEntity -> roleEntity.getCode()).toList());
        return userDTO;
    }

    public UserEntity userDtoToEntity(UserDTO dto) {
        var userEntity = new UserEntity();
        BeanUtils.copyProperties(dto, userEntity);
        BeanUtils.copyProperties(dto.getRoles(), userEntity.getRoles());
        return userEntity;
    }
    public RoleEntity roleDtoToEntity(RoleDTO dto) {
        var roleEntity = new RoleEntity();
        BeanUtils.copyProperties(dto, roleEntity);
//        if(!ObjectUtils.isEmpty(dto.getPermissions())){
//            roleEntity.setPermissions(dto.getPermissions().stream().map(AppMapper::permissionDtoToEntity).collect(Collectors.toSet()));
//            BeanUtils.copyProperties(dto.getPermissions(), roleEntity.getPermissions());
//        }
        return roleEntity;
    }
    public RoleDTO roleEntityToDTO(RoleEntity entity) {
        var roleDTO = new RoleDTO();
        BeanUtils.copyProperties(entity, roleDTO);
//        if(!ObjectUtils.isEmpty(entity.getPermissions())){
//            roleDTO.setPermissions(entity.getPermissions().stream().map(AppMapper::permissionEntityToDTO).toList());
//        }
        return roleDTO;
    }

    public RolePermissionResponseDTO roleEntityToRolePermissionResponseDTO(RoleEntity entity) {
        var dto = new RolePermissionResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        if(!ObjectUtils.isEmpty(entity.getPermissions())){
            dto.setPermissions(entity.getPermissions().stream().map(AppMapper::permissionEntityToDTO).toList());
        }
        return dto;
    }
    public PermissionEntity permissionDtoToEntity(PermissionDTO dto) {
        var permissionEntity = new PermissionEntity();
        BeanUtils.copyProperties(dto, permissionEntity);
        return permissionEntity;
    }
    public PermissionDTO permissionEntityToDTO(PermissionEntity entity) {
        var permissionDTO = new PermissionDTO();
        BeanUtils.copyProperties(entity, permissionDTO);
        return permissionDTO;
    }


    public LoginResponseDTO userEntityToLoginResponseDTO(UserEntity userEntity) {
        var loginResponseDTO = new LoginResponseDTO();
        BeanUtils.copyProperties(userEntity, loginResponseDTO);
        loginResponseDTO.setRoles(userEntity.getRoles().stream()
                .map(roleEntity -> roleEntity.getCode().name().toString())
                .collect(Collectors.toList()));
        loginResponseDTO.setPermissions(!ObjectUtils.isEmpty(userEntity.getRoles()) ?
                userEntity.getRoles().stream()
                    .flatMap(roleEntity -> roleEntity.getPermissions().stream())
                    .map(permissionEntity -> permissionEntity.getName().toUpperCase())
                .collect(Collectors.toList()): null);
        return loginResponseDTO;
    }


    public AddressEntity addressDtoToEntity(AddressDTO dto) {
        var entity = new AddressEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
    public AddressDTO addressEntityToDTO(AddressEntity entity) {
        var dto = new AddressDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setDistrictId(!ObjectUtils.isEmpty(entity.getDistrict()) ? entity.getDistrict().getId() : null);
        dto.setUpazilaId(!ObjectUtils.isEmpty(entity.getUpazila()) ? entity.getUpazila().getId() : null);
        dto.setDivisionId(!ObjectUtils.isEmpty(entity.getDivision()) ? entity.getDivision().getId() : null);
        return dto;
    }

    public CompanyEntity companyDtoToEntity(CompanyDTO dto) {
        var entity = new CompanyEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
    public CompanyDTO companyEntityToDTO(CompanyEntity entity) {
        var dto = new CompanyDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setAddress(!ObjectUtils.isEmpty(entity.getAddress()) ? addressEntityToDTO(entity.getAddress()) : null);
        return dto;
    }

}
