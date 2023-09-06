package com.mycompany.authenticationservices.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@Data
@Entity(name = "permission")
@EntityListeners(AuditingEntityListener.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PermissionEntity extends AuditModel<Long> implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private String value;
	private String description;
	private String url;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
}
