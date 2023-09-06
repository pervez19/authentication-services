package com.mycompany.authenticationservices.repository;

import com.mycompany.authenticationservices.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionEntity,Long> {
    Optional<PermissionEntity> findByNameIgnoreCase(String name);
    Optional<PermissionEntity> findByValueIgnoreCase(String value);

    Optional<PermissionEntity> findByNameAndValueIgnoreCaseAndIdNot(String name, String value, Long id);
}
