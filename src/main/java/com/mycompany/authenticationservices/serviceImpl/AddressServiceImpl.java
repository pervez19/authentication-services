package com.mycompany.authenticationservices.serviceImpl;

import com.mycompany.authenticationservices.dto.AddressDTO;
import com.mycompany.authenticationservices.entity.AddressEntity;
import com.mycompany.authenticationservices.mapper.AppMapper;
import com.mycompany.authenticationservices.repository.DistrictRepository;
import com.mycompany.authenticationservices.repository.DivisionRepository;
import com.mycompany.authenticationservices.repository.UpazilaRepository;
import com.mycompany.authenticationservices.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final DistrictRepository districtRepository;
    private final DivisionRepository divisionRepository;
    private  final UpazilaRepository upazilaRepository;
    @Override
    public AddressEntity generateAddressEntity(AddressDTO addressDTO){
        AddressEntity address= AppMapper.addressDtoToEntity(addressDTO);
        if(ObjectUtils.isNotEmpty(addressDTO.getDistrictId())){
            address.setDistrict(districtRepository.findById(addressDTO.getDistrictId()).get());
        }
        if(ObjectUtils.isNotEmpty(addressDTO.getDivisionId())){
            address.setDivision(divisionRepository.findById(addressDTO.getDivisionId()).get());
        }
        if(ObjectUtils.isNotEmpty(addressDTO.getUpazilaId())){
            address.setUpazila(upazilaRepository.findById(addressDTO.getUpazilaId()).get());
        }
        return address;
    }

}
