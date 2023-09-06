package com.mycompany.authenticationservices.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.authenticationservices.enums.IndustryTypeEnum;
import com.mycompany.authenticationservices.enums.TeamSizeEnum;
import com.mycompany.authenticationservices.helper.MyGenerator;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Cache;

import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
@Entity(name = "company")
@JsonIgnoreProperties(ignoreUnknown = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CompanyEntity  extends AuditModel<Long> implements Serializable {
    @Id
    @GeneratedValue(generator = MyGenerator.generatorName)
    @GenericGenerator(name = MyGenerator.generatorName, strategy = "com.mycompany.authenticationservices.helper.MyGenerator")
    private String accountId;

    @Column(length = 250)
    private String name;

    @Column(length = 500)
    private String motto;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @Column(name = "industry_type")
    @Enumerated(EnumType.STRING)
    private IndustryTypeEnum industryType;

    @Column(name = "team_size")
    @Enumerated(EnumType.STRING)
    private TeamSizeEnum teamSize;

    @Column(name = "website_address",length = 250)
    private String  websiteAddress;
}
