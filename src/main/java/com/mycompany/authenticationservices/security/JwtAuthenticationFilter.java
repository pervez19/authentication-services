package com.mycompany.authenticationservices.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenService jwtTokenService;
	private final TokenRevocationService tokenRevocationService;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(
			 HttpServletRequest request,
			 HttpServletResponse response,
			 FilterChain filterChain
	) throws ServletException, IOException {
		Optional<String> jwt = jwtTokenService.resolveToken(request.getHeader(HttpHeaders.AUTHORIZATION));
		if (jwt.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				final Claims claims = jwtTokenService.parseAccessToken(jwt.get());
				if (tokenRevocationService.isRevoked(claims)) {
					throw new JwtException("Token has been revoked");
				}
				final String username = claims.getSubject();
				if (StringUtils.isNotEmpty(username)) {
					// Loads only active users and checks enabled/locked/expired, so a disabled user is rejected.
					final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			} catch (final JwtException | AuthenticationException ex) {
				// Stay unauthenticated: protected endpoints get 401 from the entry point, public ones still work.
				log.warn("Rejected bearer token: {}", ex.getMessage());
			}
		}
		filterChain.doFilter(request, response);
	}
}
