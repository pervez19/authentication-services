package com.mycompany.authenticationservices.controller;


import com.mycompany.authenticationservices.dto.*;
import com.mycompany.authenticationservices.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("Sign in attempt from  user: " + loginDTO.username());
        LoginResponseDTO authenticationResponse = authenticationService.authenticateUser(loginDTO);
        return ResponseEntity.ok(new Response("success", "Login Successful", authenticationResponse));
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registration(@Valid @RequestBody SignupDTO signupDTO) {
        log.info("Sign up attempt from  user: " + signupDTO.getUsername());
        UserDTO userDTO = authenticationService.registration(signupDTO);
        return ResponseEntity.ok(new Response("success", "Registration Successfully", userDTO));
    }
}
