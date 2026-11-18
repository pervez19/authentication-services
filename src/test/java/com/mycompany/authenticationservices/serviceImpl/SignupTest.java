package com.mycompany.authenticationservices.serviceImpl;

import com.mycompany.authenticationservices.dto.CompanyDTO;
import com.mycompany.authenticationservices.dto.SignupDTO;
import com.mycompany.authenticationservices.dto.UserDTO;
import com.mycompany.authenticationservices.entity.RoleEntity;
import com.mycompany.authenticationservices.entity.UserEntity;
import com.mycompany.authenticationservices.enums.RoleEnum;
import com.mycompany.authenticationservices.repository.RoleRepository;
import com.mycompany.authenticationservices.repository.UserRepository;
import com.mycompany.authenticationservices.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SignupTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final CompanyService companyService = mock(CompanyService.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final AuthenticationServiceImpl service = new AuthenticationServiceImpl(
            null, null, null, null, passwordEncoder, userRepository, roleRepository, companyService);

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(roleRepository.findByCode(RoleEnum.ROLE_ADMIN))
                .thenReturn(Optional.of(new RoleEntity().setCode(RoleEnum.ROLE_ADMIN)));
        when(companyService.save(any())).thenReturn(new CompanyDTO().setAccountId("ACC-1"));
    }

    @Test
    void signupWithoutRolesCreatesCompanyAdmin() {
        SignupDTO signup = new SignupDTO();
        signup.setName("Alice Smith");
        signup.setEmail("Alice@Example.com");
        signup.setPassword("secret123"); // no "roles" in the request

        UserDTO created = service.registration(signup);

        ArgumentCaptor<UserEntity> saved = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getEmail()).isEqualTo("alice@example.com");
        assertThat(saved.getValue().getPassword()).isEqualTo("hashed");
        assertThat(saved.getValue().getAccountId()).isEqualTo("ACC-1");
        assertThat(created.getRoles()).containsExactly(RoleEnum.ROLE_ADMIN);
    }
}
