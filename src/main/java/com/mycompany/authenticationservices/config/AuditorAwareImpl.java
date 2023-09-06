package com.mycompany.authenticationservices.config;

import java.util.Optional;

import com.mycompany.authenticationservices.entity.UserEntity;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<Long> {
	@Override
	public Optional<Long> getCurrentAuditor() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken)
			return Optional.empty();
		final UserEntity userEntity = (UserEntity) authentication.getPrincipal();
		return Optional.ofNullable(userEntity.getId());
	}
}
