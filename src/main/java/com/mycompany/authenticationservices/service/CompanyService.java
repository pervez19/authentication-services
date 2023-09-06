package com.mycompany.authenticationservices.service;


import com.mycompany.authenticationservices.dto.CompanyDTO;

import java.util.List;

public interface CompanyService {
    CompanyDTO save(CompanyDTO companyDTO);

    CompanyDTO getByAccountId(String accountId);

    List<CompanyDTO> getAll();

    CompanyDTO updateByAccountId(String accountId, CompanyDTO companyDTO);

    void delete(String accountId);
}
