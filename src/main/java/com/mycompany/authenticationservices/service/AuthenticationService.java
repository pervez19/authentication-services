package com.mycompany.authenticationservices.service;


import com.mycompany.authenticationservices.dto.LoginDTO;
import com.mycompany.authenticationservices.dto.LoginResponseDTO;
import com.mycompany.authenticationservices.dto.SignupDTO;
import com.mycompany.authenticationservices.dto.UserDTO;

public interface AuthenticationService {
    LoginResponseDTO authenticateUser (LoginDTO loginDTO);

    UserDTO registration(SignupDTO signupDTO);
}
