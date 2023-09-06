package com.mycompany.authenticationservices.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtConfig jwtConfig;
	private final JwtTokenService jwtTokenService;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(
			 HttpServletRequest request,
			 HttpServletResponse response,
			 FilterChain filterChain
	) throws ServletException, IOException {
		try {
			Optional<String> jwt = resolveHeaderToken(request);
			if (jwt.isPresent() && jwtTokenService.validateToken(jwt.get())) {

				final String username = jwtTokenService.extractUsernameFromJWT(jwt.get());

				if(StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null ){

					final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

					if(jwtTokenService.isTokenValid(jwt.get(),userDetails))
					{
						final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						authenticationToken.setDetails(
								new WebAuthenticationDetailsSource().buildDetails(request)
						);
						SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					}
				}
			}
		} catch (final Exception ex) {
			JwtAuthenticationFilter.log.error("Error log in {}", ex.getMessage());
			response.setHeader("error", ex.getMessage());
			response.setStatus(HttpStatus.FORBIDDEN.value());
			final Map<String, String> errors = new HashMap<>();
			errors.put("error_message", ex.getMessage());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			new ObjectMapper().writeValue(response.getOutputStream(), errors);
		}
		filterChain.doFilter(request, response);
	}

	private Optional<String> resolveHeaderToken(HttpServletRequest request) {
		final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		return authorizationHeader != null && authorizationHeader.startsWith(jwtConfig.getPrefix() + " ") ?
				Optional.of(authorizationHeader.substring((jwtConfig.getPrefix() + " ").length())) : Optional.empty();
	}
}
