package com.mycompany.authenticationservices.service;


import com.mycompany.authenticationservices.dto.AddressDTO;
import com.mycompany.authenticationservices.entity.AddressEntity;

public interface AddressService {
    AddressEntity generateAddressEntity(AddressDTO addressDTO);
}
