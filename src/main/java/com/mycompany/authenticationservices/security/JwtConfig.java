package com.mycompany.authenticationservices.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

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

	private final PrivateKey privateKey;
	private final PublicKey publicKey;

	// RS256: tokens are signed with the private key (only this service has it);
	// gateway and other services verify with the public key.
	public JwtConfig(@Value("${applications.jwtConfig.privateKey}") String privateKey,
					 @Value("${applications.jwtConfig.publicKey}") String publicKey) throws GeneralSecurityException {
		KeyFactory rsa = KeyFactory.getInstance("RSA");
		this.privateKey = rsa.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
		this.publicKey = rsa.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
	}
}
