package com.mycompany.authenticationservices.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The persistent class for the division database table.
 * @author Awlad Hossain
 * @since 08 February, 2022
 * @version 1.0
 */

@Data
@Entity(name = "upazila")
@EntityListeners(AuditingEntityListener.class)
@Cache( usage = CacheConcurrencyStrategy.READ_WRITE)
public class UpazilaEntity extends AuditModel<Long>{
    private static final long serialVersionUID = 94351967198939425L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Column(name = "bn_name")
    private String bnName;

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
    @JoinColumn(name = "district_id")
    private DistrictEntity district;
}
