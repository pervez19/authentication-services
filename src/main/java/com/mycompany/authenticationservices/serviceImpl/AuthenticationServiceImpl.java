package com.mycompany.authenticationservices.serviceImpl;


import com.mycompany.authenticationservices.dto.*;
import com.mycompany.authenticationservices.entity.RoleEntity;
import com.mycompany.authenticationservices.entity.UserEntity;
import com.mycompany.authenticationservices.enums.RoleEnum;
import com.mycompany.authenticationservices.exception.DuplicateException;
import com.mycompany.authenticationservices.mapper.AppMapper;
import com.mycompany.authenticationservices.repository.RoleRepository;
import com.mycompany.authenticationservices.repository.UserRepository;
import com.mycompany.authenticationservices.security.JwtTokenService;
import com.mycompany.authenticationservices.service.AuthenticationService;
import com.mycompany.authenticationservices.service.CompanyService;
import com.mycompany.authenticationservices.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.beans.Transient;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final CompanyService companyService;

    @Override
    public LoginResponseDTO authenticateUser(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.username(),
                        loginDTO.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserEntity user = (UserEntity) userService.loadUserByUsername(loginDTO.username());

        return AppMapper.userEntityToLoginResponseDTO(user)
                .setToken(jwtTokenService.doGenerateToken(authentication))
                .setTokenDuration(jwtTokenService.getExpirationTime());
    }

    @Override
    @Transient
    public UserDTO registration(SignupDTO signupDTO){
        signupDTO.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        if (userRepository.findByUsername(signupDTO.getUsername()).isPresent()) {
            throw new DuplicateException("Username Already exist !!");
        }
        if (userRepository.findByEmail(signupDTO.getEmail()).isPresent()) {
            throw new DuplicateException("Email Already exist !!");
        }

        UserEntity userEntity = AppMapper.userDtoToEntity(signupDTO)
                .setPassword(signupDTO.getPassword())
                .setRoles(signupDTO.getRoles().stream().map(roleEnum -> roleRepository.findByCode(roleEnum).get()).collect(Collectors.toSet()));

        if (ObjectUtils.isEmpty(userEntity.getRoles()) || userEntity.getRoles().size() <= 0) {
            Set<RoleEntity> roleEntitySet = new HashSet<>();
            roleEntitySet.add(roleRepository.findByCode(RoleEnum.ROLE_ADMIN).get());
            userEntity.setRoles(roleEntitySet);
        }
        CompanyDTO companyDTO = companyService.save(new CompanyDTO());
        if (!ObjectUtils.isEmpty(companyDTO) && !ObjectUtils.isEmpty(StringUtils.trimToNull(companyDTO.getAccountId())))
                userEntity.setAccountId(companyDTO.getAccountId());
        return AppMapper.userEntityToDto(userRepository.save(userEntity));
    }
}
