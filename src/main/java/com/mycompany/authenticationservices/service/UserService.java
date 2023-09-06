package com.mycompany.authenticationservices.service;

import java.util.List;


import com.mycompany.authenticationservices.dto.SignupDTO;
import com.mycompany.authenticationservices.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService, UserDetailsPasswordService {
	void deleteById(Long id);
	Page<UserDTO> getUsers(Pageable pageable);
	UserDTO getById(Long id);
	UserDTO updateUser(UserDTO newUser, Long id);
	UserDTO patchUser(UserDTO newUser, Long id);
	List<UserDTO> getUsers();

    List<UserDTO> getByAccountId(String accountId);

    UserDTO createUser(SignupDTO signupDTO);
}
