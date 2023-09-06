package com.mycompany.authenticationservices.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt", "createdBy", "updatedBy"}, allowGetters = true, ignoreUnknown = true)
public abstract class AuditModel<U> implements Serializable {
	private static final long serialVersionUID = -6627873277532339968L;

	@Column(name = "uuid", updatable = false)
	private String uuid;

	@CreatedBy
	@Column(name = "created_by", updatable = false)
	private U createdBy;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreatedDate
	private Long createdAt;

	@LastModifiedBy
	@Column(name = "updated_by")
	private U updatedBy;

	@Column(name = "updated_at",nullable = false)
	@LastModifiedDate
	private Long updatedAt;

	@PrePersist
	protected void onCreateUuid() {
		setUuid(UUID.randomUUID().toString().replace("-",""));
	}
}
