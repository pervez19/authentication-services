package com.mycompany.authenticationservices.security;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mycompany.authenticationservices.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
	public static final String TOKEN_TYPE_CLAIM = "token_type";
	public static final String REFRESH_TOKEN_TYPE = "refresh";
	private final JwtConfig jwtConfig;

	public String doGenerateToken(Authentication authentication) {
		final UserEntity user = (UserEntity) authentication.getPrincipal();
		return Jwts
			.builder()
			.setId(UUID.randomUUID().toString()) // jti: lets a single token be revoked (logout)
			.setSubject(user.getUsername())
			.claim("authorities",
					user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
							.collect(Collectors.toList()))
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
			.signWith(jwtConfig.getPrivateKey(), SignatureAlgorithm.RS256)
			.compact();
	}

	public String doGenerateRefreshToken(Authentication authentication) {
		final UserEntity user = (UserEntity) authentication.getPrincipal();
		return Jwts.builder()
				.setId(UUID.randomUUID().toString())
				.setSubject(user.getUsername())
				.claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE) // verifiers reject it as an access token
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshExpiration()))
				.signWith(jwtConfig.getPrivateKey(), SignatureAlgorithm.RS256)
				.compact();
	}

	/**
	 * Verifies signature and expiry of an access token in a single parse.
	 *
	 * @throws JwtException if the token is invalid, expired, or is a refresh token
	 */
	public Claims parseAccessToken(String token) {
		Claims claims = parse(token);
		if (REFRESH_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM))) {
			throw new UnsupportedJwtException("Refresh token cannot be used as access token");
		}
		return claims;
	}

	/**
	 * Verifies signature and expiry of a refresh token.
	 *
	 * @throws JwtException if the token is invalid, expired, or is not a refresh token
	 */
	public Claims parseRefreshToken(String token) {
		Claims claims = parse(token);
		if (!REFRESH_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM))) {
			throw new UnsupportedJwtException("Access token cannot be used as refresh token");
		}
		return claims;
	}

	private Claims parse(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(jwtConfig.getPublicKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public Optional<String> resolveToken(String authorizationHeader) {
		final String prefix = jwtConfig.getPrefix() + " ";
		return authorizationHeader != null && authorizationHeader.startsWith(prefix)
				? Optional.of(authorizationHeader.substring(prefix.length()))
				: Optional.empty();
	}

	public int getExpirationTime(){
		return jwtConfig.getExpiration();
	}

	public int getRefreshExpirationTime(){
		return jwtConfig.getRefreshExpiration();
	}
}
