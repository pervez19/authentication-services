package com.mycompany.authenticationservices.entity;

import com.mycompany.authenticationservices.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.Cache;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "role")
@Accessors(chain = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RoleEntity extends AuditModel<Long> implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(length = 150)
	private RoleEnum code;

	private String description;

	@Column(name = "welcome_page_url")
	private String welcomePageURL;

	@ManyToMany(cascade = { CascadeType.DETACH, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinTable(name = "Role_Permission",
			joinColumns = { @JoinColumn(name = "role_id", updatable = true) },
			inverseJoinColumns = { @JoinColumn(name = "permission_id", updatable = true) })
	private Set<PermissionEntity> permissions = new HashSet<>();

	@Column(name = "is_deleted")
	private Boolean isDeleted;
}
