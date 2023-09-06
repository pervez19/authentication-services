package com.mycompany.authenticationservices.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.security.Key;

@Getter
@Setter
@Component
public class JwtConfig {

	@Value("${applications.jwtConfig.header}")
	private String header;

	@Value("${applications.jwtConfig.prefix}")
	private String prefix;

	@Value("${applications.jwtConfig.expirationDateInMs}")
	private int expiration;

	@Value("${applications.jwtConfig.refreshExpirationDateInMs}")
	private int refreshExpiration;

	@Value("${applications.jwtConfig.secret}")
	private String secret;

	public Key getSecret(){
		byte[] keyByte= Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyByte);
	}
}
