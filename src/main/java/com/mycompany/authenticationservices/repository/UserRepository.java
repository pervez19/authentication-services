package com.mycompany.authenticationservices.repository;

import java.util.List;
import java.util.Optional;

import com.mycompany.authenticationservices.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	// Emails are matched case-insensitively: "Alice@Mail.com" and "alice@mail.com" are the same account.
	@Query("SELECT u FROM users u WHERE lower(u.email) = lower(:email) AND u.enabled = true")
    Optional<UserEntity> findActiveByEmail(@Param("email") String email);
	@Query("SELECT u FROM users u WHERE lower(u.email) = lower(:email)")
	Optional<UserEntity> findByEmail(@Param("email") String email);

	Optional<UserEntity> findByEmailIgnoreCaseAndIdNot(String email, Long id);

	List<UserEntity> findByAccountId(String accountId);
}