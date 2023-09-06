package com.mycompany.authenticationservices.repository;

import com.mycompany.authenticationservices.entity.RoleEntity;
import com.mycompany.authenticationservices.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity,Long> {
    Optional<RoleEntity> findByCode(RoleEnum roleName);

    Optional<RoleEntity> findByCodeAndIdNot(RoleEnum name, long id);
}
