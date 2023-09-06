package com.mycompany.authenticationservices.serviceImpl;


import com.mycompany.authenticationservices.dto.CompanyDTO;
import com.mycompany.authenticationservices.entity.CompanyEntity;
import com.mycompany.authenticationservices.exception.DataNotFoundException;
import com.mycompany.authenticationservices.mapper.AppMapper;
import com.mycompany.authenticationservices.repository.CompanyRepository;
import com.mycompany.authenticationservices.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final AddressServiceImpl addressService;

    @Override
    public CompanyDTO save(CompanyDTO companyDTO) {
        CompanyEntity company= AppMapper.companyDtoToEntity(companyDTO);
        company.setAddress(ObjectUtils.isNotEmpty(companyDTO.getAddress())?addressService.generateAddressEntity(companyDTO.getAddress()):null);
        return AppMapper.companyEntityToDTO(companyRepository.save(company));
    }

    @Override
    public CompanyDTO getByAccountId(String accountId) {
        return AppMapper.companyEntityToDTO(companyRepository.findByAccountId(accountId)
                .orElseThrow(() -> new DataNotFoundException("Company not found by this account id : "+accountId)));
    }

    @Override
    public List<CompanyDTO> getAll() {
        return companyRepository.findAll().stream().map(companyEntity -> AppMapper.companyEntityToDTO(companyEntity)).collect(Collectors.toList());
    }

    @Override
    public CompanyDTO updateByAccountId(String accountId, CompanyDTO companyDTO) {
        return AppMapper.companyEntityToDTO(companyRepository.findByAccountId(accountId).map(companyEntity -> {
            if(!ObjectUtils.isEmpty(StringUtils.trimToNull(companyDTO.getName()))){
                companyEntity.setName(companyDTO.getName().trim());
            }
            if(!ObjectUtils.isEmpty(StringUtils.trimToNull(companyDTO.getMotto()))){
                companyEntity.setMotto(companyDTO.getMotto().trim());
            }
            if(!ObjectUtils.isEmpty(companyDTO.getIndustryType())){
                companyEntity.setIndustryType(companyDTO.getIndustryType());
            }
            if(!ObjectUtils.isEmpty(StringUtils.trimToNull(companyDTO.getWebsiteAddress()))){
                companyEntity.setWebsiteAddress(companyDTO.getWebsiteAddress().trim());
            }
            if(!ObjectUtils.isEmpty(companyDTO.getTeamSize())){
                companyEntity.setTeamSize(companyDTO.getTeamSize());
            }
            if(!ObjectUtils.isEmpty(companyDTO.getAddress())){
                companyEntity.setAddress(addressService.generateAddressEntity(companyDTO.getAddress()));
            }
            return companyRepository.save(companyEntity);
        }).orElseThrow(() -> new DataNotFoundException("Company not found by this "+accountId)));
    }

    @Override
    public void delete(String accountId) {
        CompanyEntity companyEntity = companyRepository.findByAccountId(accountId)
                .orElseThrow(() -> new DataNotFoundException("Company not found by this "+accountId));
        companyRepository.delete(companyEntity);
    }
}
