package com.mycompany.authenticationservices.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

import java.io.Serializable;

/**
 * the persistent class for district database table
 *
 * @author pervez
 * @since 08 February, 2022
 * @version 1.0
 */


@Data
@EqualsAndHashCode
@Entity(name = "district")
@EntityListeners(AuditingEntityListener.class)
@Cache( usage = CacheConcurrencyStrategy.READ_WRITE)
public class DistrictEntity extends AuditModel<Long> implements Serializable {

	private static final long serialVersionUID = 94351967198939425L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	@Column(name = "bn_name")
	private String bnName;

	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "division_id")
	private DivisionEntity division;

	@Column(name = "short_name")
	private String shortName;


}
