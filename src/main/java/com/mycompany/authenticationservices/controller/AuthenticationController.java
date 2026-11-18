package com.mycompany.authenticationservices.controller;


import com.mycompany.authenticationservices.dto.*;
import com.mycompany.authenticationservices.security.JwtTokenService;
import com.mycompany.authenticationservices.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * This controller is to provide all the authentication relevant api's
 *
 * @author pervez
 * @version 1.0
 * @since 01 January 2023
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("Sign in attempt from  user: " + loginDTO.username());
        LoginResponseDTO authenticationResponse = authenticationService.authenticateUser(loginDTO);
        return ResponseEntity.ok(new Response("success", "Login Successful", authenticationResponse));
    }


    /** Public (the access token may already be expired). Returns a new access + refresh token pair. */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        LoginResponseDTO tokens = authenticationService.refreshToken(refreshTokenDTO.refreshToken());
        return ResponseEntity.ok(new Response("success", "Token Refreshed Successfully", tokens));
    }

    /**
     * Requires a valid access token. Send the refresh token in the body too, otherwise it can still
     * mint new access tokens until it expires.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                    @RequestBody(required = false) RefreshTokenDTO refreshTokenDTO) {
        authenticationService.logout(jwtTokenService.resolveToken(authorization).orElseThrow(),
                refreshTokenDTO != null ? refreshTokenDTO.refreshToken() : null);
        return ResponseEntity.ok(new Response("success", "Logout Successful", null));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registration(@Valid @RequestBody SignupDTO signupDTO) {
        log.info("Sign up attempt from  user: " + signupDTO.getUsername());
        UserDTO userDTO = authenticationService.registration(signupDTO);
        return ResponseEntity.ok(new Response("success", "Registration Successfully", userDTO));
    }
}
