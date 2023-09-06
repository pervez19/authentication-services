package com.mycompany.authenticationservices.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
@Entity(name = "users")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class UserEntity extends AuditModel<Long> implements Serializable, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @Column(name = "first_name", length = 50)
    private String firstName;

    @NotNull
    @Column(name = "last_name", length = 50)
    private String lastName;

    @NotNull(message = "User Name cannot be null")
    @NotEmpty(message = "User Name cannot be empty")
    @Column(length = 50, unique = true, nullable = false)
    @Size(min = 2, max = 50, message = "Name should have at least 2 characters to 50")
    private String username;

    @NotNull(message = "accountId cannot be null")
    @NotEmpty(message = "accountId cannot be empty")
    private String accountId;

    @NotNull(message = "Password cannot be null")
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 8, message = "passWord should have atleast 8 characters")
    @JsonIgnore
    private String password;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email is not valid", regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    @NotEmpty(message = "Email cannot be empty")
    @Column(unique = true, name = "email", length = 254)
    @Size(min = 5, max = 254)
    private String email;

    private boolean enabled = true;

    @Column(name = "account_non_expired")
    private boolean accountNonExpired;

    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = true)}, inverseJoinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "id", updatable = true)})
    private Set<RoleEntity> roles = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authority = roles.stream().map(role -> new SimpleGrantedAuthority(role.getCode().name())).collect(Collectors.toSet());
        authority.addAll(roles.stream().flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getValue())).collect(Collectors.toSet()));
        return authority;
    }
}
