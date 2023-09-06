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

	@Query("SELECT u FROM users u WHERE u.email = :email AND u.enabled = true")
    Optional<UserEntity> findActiveByEmail(@Param("email") String email);
	@Query("SELECT u FROM users u WHERE u.username = :username AND u.enabled = true")
	Optional<UserEntity> findActiveByUsername(@Param("username") String username);
	@Query("SELECT DISTINCT u FROM users u WHERE u.username = :username")
	Optional<UserEntity> findByUsername(@Param("username") String username);
	@Query("SELECT u FROM users u WHERE u.email = :email")
	Optional<UserEntity> findByEmail(@Param("email") String email);

	Optional<UserEntity> findByUsernameAndIdNot(String username, Long id);

	Optional<UserEntity> findByEmailAndIdNot(String email, Long id);

	List<UserEntity> findByAccountId(String accountId);
}