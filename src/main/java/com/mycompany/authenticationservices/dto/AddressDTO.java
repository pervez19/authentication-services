package com.mycompany.authenticationservices.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class AddressDTO {
    private Long id;
    private String detailAddress;
    private String others;
    private Long divisionId;
    private Long districtId;
    private Long upazilaId;
}

