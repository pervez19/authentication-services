package com.mycompany.authenticationservices.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

/**
 * The persistent class for the division database table.
 *
 * @author pervez
 * @since 08 February, 2022
 * @version 1.0
 */

@Data
@Entity(name = "address")
@Accessors(chain = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AddressEntity extends AuditModel<Long> implements Serializable {

	private static final long serialVersionUID = 94351967198939425L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(columnDefinition = "TEXT", name = "detail_address")
	private String detailAddress;

	@Column(columnDefinition = "TEXT")
	private String others;

	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "division_id")
	private DivisionEntity division;

	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "district_id")
	private DistrictEntity district;

	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "upazila_id")
	private UpazilaEntity upazila;
}
