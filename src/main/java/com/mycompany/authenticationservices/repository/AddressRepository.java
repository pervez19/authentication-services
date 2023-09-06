package com.mycompany.authenticationservices.repository;

import com.mycompany.authenticationservices.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity,Long> {
}
