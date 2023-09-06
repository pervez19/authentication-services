package com.mycompany.authenticationservices.repository;

import com.mycompany.authenticationservices.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyEntity,String> {
    Optional<CompanyEntity> findByAccountId(String accountId);
}
