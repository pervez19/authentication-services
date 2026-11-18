package com.mycompany.authenticationservices.service;


import com.mycompany.authenticationservices.dto.LoginDTO;
import com.mycompany.authenticationservices.dto.LoginResponseDTO;
import com.mycompany.authenticationservices.dto.SignupDTO;
import com.mycompany.authenticationservices.dto.UserDTO;

public interface AuthenticationService {
    LoginResponseDTO authenticateUser (LoginDTO loginDTO);

    UserDTO registration(SignupDTO signupDTO);

    /** Issues a new access + refresh token pair; the given refresh token can't be used again (rotation). */
    LoginResponseDTO refreshToken(String refreshToken);

    /**
     * Revokes the access token (raw JWT, without "Bearer ") and, if given, the refresh token of the same user.
     */
    void logout(String accessToken, String refreshToken);
}
