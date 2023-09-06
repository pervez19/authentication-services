package com.mycompany.authenticationservices.entity;

import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity(name = "module")
@EntityListeners(AuditingEntityListener.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ModuleEntity extends AuditModel<Long> implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank(message = "Name is mandatory")
	private String name;

	@NotBlank(message = "URL is mandatory")
	private String url;

	private String description;

	@Column(name = "is_deleted")
	private Boolean isDeleted;
}
