package com.mycompany.authenticationservices.exception;

import io.jsonwebtoken.JwtException;

public class SignatureException extends JwtException {

	public SignatureException(String message) {
		super(message);
	}

	public SignatureException(String message, Throwable cause) {
		super(message, cause);
	}
}
