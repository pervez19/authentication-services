package com.mycompany.authenticationservices.entity;

import java.io.Serializable;

import com.mycompany.authenticationservices.enums.Gender;
import com.mycompany.authenticationservices.enums.Religion;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;


@Data
@Entity(name = "user_profile")
@EntityListeners(AuditingEntityListener.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserProfileEntity extends AuditModel<Long> implements Serializable {

	private static final long serialVersionUID = -2723166795835115030L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne(cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "permanent_address_id")
	private AddressEntity permanentAddress;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "present_address_id")
	private AddressEntity presentAddress;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "signature_url")
	private String signatureUrl;

	private String nid;
	@Column(name = "birth_registration_number")
	private String birthRegistrationNumber;

	@Past
	@Column(name = "birth_date")
	private Long birthDate;
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Enumerated(EnumType.STRING)
	private Religion religion;

	@Column(name = "is_deleted")
	private Boolean isDeleted;
}
