package com.mycompany.authenticationservices.dto;

import com.mycompany.authenticationservices.enums.IndustryTypeEnum;
import com.mycompany.authenticationservices.enums.TeamSizeEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class CompanyDTO {
    private String accountId;
    private String name;
    private String motto;
    private AddressDTO address;
    @Enumerated(EnumType.STRING)
    private IndustryTypeEnum industryType;
    @Enumerated(EnumType.STRING)
    private TeamSizeEnum teamSize;
    private String  websiteAddress;

}
