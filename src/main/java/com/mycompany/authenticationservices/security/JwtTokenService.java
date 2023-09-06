package com.mycompany.authenticationservices.security;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mycompany.authenticationservices.entity.UserEntity;
import com.mycompany.authenticationservices.exception.SignatureException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);
	private final JwtConfig jwtConfig;

	public String doGenerateToken(Authentication authentication) {
		final UserEntity user = (UserEntity) authentication.getPrincipal();
		return Jwts
			.builder()
			.setSubject(user.getUsername())
			.claim("authorities",
					user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
							.collect(Collectors.toList()))
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
			.signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
			.compact();
	}
	public String doGenerateRefreshToken(Authentication authentication) {

		final UserEntity user = (UserEntity) authentication.getPrincipal();
		return Jwts.builder().setSubject(user.getUsername()).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshExpiration()))
				.signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret()).compact();
	}

	public String extractUsernameFromJWT(String token) {
		return extractClaimFromToken(token,Claims::getSubject);
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(authToken);
			return true;
		} catch (final SignatureException ex) {
			JwtTokenService.logger.error("Invalid JWT signature");
		} catch (final MalformedJwtException ex) {
			JwtTokenService.logger.error("Invalid JWT token");
		} catch (final ExpiredJwtException ex) {
			JwtTokenService.logger.error("Expired JWT token");
		} catch (final UnsupportedJwtException ex) {
			JwtTokenService.logger.error("Unsupported JWT token");
		} catch (final IllegalArgumentException ex) {
			JwtTokenService.logger.error("JWT claims string is empty.");
		}
		return false;
	}

	public <T> T extractClaimFromToken(String authToken, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaimsFromToken(authToken);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaimsFromToken(String authToken) {
		return Jwts
				.parser()
				.setSigningKey(jwtConfig.getSecret())
				.parseClaimsJws(authToken)
				.getBody();
	}
	private Boolean isTokenExpired(String authToken) {
		return extractExpirationFromToken(authToken).before(new Date());
	}

	public Date extractExpirationFromToken(String authToken) {
		return extractClaimFromToken(authToken, Claims::getExpiration);
	}

	public boolean isTokenValid(String authToken, UserDetails userDetails) {
		final String userName = extractUsernameFromJWT(authToken);
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(authToken));
	}

	public int getExpirationTime(){
		return jwtConfig.getExpiration();
	}
}
