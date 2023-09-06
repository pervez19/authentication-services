package com.mycompany.authenticationservices.serviceImpl;


import com.mycompany.authenticationservices.dto.PermissionDTO;
import com.mycompany.authenticationservices.entity.PermissionEntity;
import com.mycompany.authenticationservices.exception.DataNotFoundException;
import com.mycompany.authenticationservices.exception.DuplicateException;
import com.mycompany.authenticationservices.mapper.AppMapper;
import com.mycompany.authenticationservices.repository.PermissionRepository;
import com.mycompany.authenticationservices.repository.RoleRepository;
import com.mycompany.authenticationservices.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    private final RoleRepository roleRepository;

    @Override
    public PermissionEntity getPermissionById(Long id) {
        return permissionRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Permission not found for this id" + id));
    }

    @Override
    public PermissionDTO save(PermissionDTO permissionDTO) {
        if (permissionRepository.findByNameIgnoreCase(permissionDTO.getName()).isPresent()) {
            throw new DuplicateException("Permission Already exist !!");
        }
        if (permissionRepository.findByValueIgnoreCase(permissionDTO.getValue()).isPresent()) {
            throw new DuplicateException("Permission Value Already exist !!");
        }
        return AppMapper.permissionEntityToDTO(permissionRepository.save(AppMapper.permissionDtoToEntity(permissionDTO)));
    }

    @Override
    public List<PermissionDTO> getPermissions() {
        return permissionRepository.findAll().stream().map(permissionEntity -> AppMapper.permissionEntityToDTO(permissionEntity)).collect(Collectors.toList());
    }

    private void checkUpdatedPermissionIsExist(PermissionDTO permissionDTO) {
        if (permissionRepository.findByNameAndValueIgnoreCaseAndIdNot(permissionDTO.getName(),permissionDTO.getValue(), permissionDTO.getId()).isPresent()) {
            throw new DuplicateException("Permission Already exist !!");
        }
    }

    @Override
    public PermissionDTO update(PermissionDTO newpermissionDTO) {
        checkUpdatedPermissionIsExist(newpermissionDTO);
        return AppMapper.permissionEntityToDTO(permissionRepository.findById(newpermissionDTO.getId()).map(permission -> {
            if (!ObjectUtils.isEmpty(StringUtils.trimToNull(newpermissionDTO.getName()))) {
                permission.setName(newpermissionDTO.getName().trim());
            }
            if (!ObjectUtils.isEmpty(StringUtils.trimToNull(newpermissionDTO.getDescription()))) {
                permission.setDescription(newpermissionDTO.getDescription().trim());
            }
            if (!ObjectUtils.isEmpty(StringUtils.trimToNull(newpermissionDTO.getValue()))) {
                permission.setValue(newpermissionDTO.getValue().trim());
            }
            if (!ObjectUtils.isEmpty(StringUtils.trimToNull(newpermissionDTO.getUrl()))) {
                permission.setUrl(newpermissionDTO.getUrl().trim());
            }
            return permissionRepository.save(permission);
        }).orElseThrow(() -> new DataNotFoundException("Permission not found for this id" + newpermissionDTO.getId())));
    }

    @Override
    public void delete(Long permissionId) {
        permissionRepository.findById(permissionId).orElseThrow(() -> new DataNotFoundException("Permission not found for this id" + permissionId));
        permissionRepository.deleteById(permissionId);
    }
}
