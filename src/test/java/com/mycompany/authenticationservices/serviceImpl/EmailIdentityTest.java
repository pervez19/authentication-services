package com.mycompany.authenticationservices.serviceImpl;

import com.mycompany.authenticationservices.dto.SignupDTO;
import com.mycompany.authenticationservices.dto.UserDTO;
import com.mycompany.authenticationservices.entity.UserEntity;
import com.mycompany.authenticationservices.exception.DuplicateException;
import com.mycompany.authenticationservices.repository.UserRepository;
import com.mycompany.authenticationservices.security.TokenRevocationService;
import com.mycompany.authenticationservices.service.RoleService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Users are identified by email (no username) and have a single "name". */
class EmailIdentityTest {

    private final UserRepository repository = mock(UserRepository.class);
    private final TokenRevocationService revocation = mock(TokenRevocationService.class);
    private final UserDetailsServiceImpl service =
            new UserDetailsServiceImpl(repository, mock(RoleService.class), revocation);

    @Test
    void springSecurityUsernameIsTheEmail() {
        assertThat(new UserEntity().setEmail("alice@example.com").getUsername()).isEqualTo("alice@example.com");
    }

    @Test
    void loginLooksUpActiveUserByTrimmedEmail() {
        UserEntity alice = activeUser("alice@example.com");
        when(repository.findActiveByEmail("Alice@Example.com")).thenReturn(Optional.of(alice));
        assertThat(service.loadUserByUsername("  Alice@Example.com ")).isSameAs(alice);
    }

    @Test
    void unknownEmailIsRejected() {
        when(repository.findActiveByEmail(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.loadUserByUsername("nobody@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
        assertThatThrownBy(() -> service.loadUserByUsername("  "))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void createUserStoresNormalizedEmailAndName() {
        when(repository.findByEmail(any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        SignupDTO signup = new SignupDTO();
        signup.setEmail("  Alice@Example.COM ");
        signup.setName("Alice Smith");
        signup.setPassword("secret123");
        signup.setRoles(List.of());

        UserDTO created = service.createUser(signup);

        ArgumentCaptor<UserEntity> saved = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getEmail()).isEqualTo("alice@example.com");
        assertThat(saved.getValue().getName()).isEqualTo("Alice Smith");
        assertThat(created.getEmail()).isEqualTo("alice@example.com");
        assertThat(created.getName()).isEqualTo("Alice Smith");
    }

    @Test
    void duplicateEmailIsRejected() {
        when(repository.findByEmail("alice@example.com")).thenReturn(Optional.of(activeUser("alice@example.com")));
        SignupDTO signup = new SignupDTO();
        signup.setEmail("ALICE@example.com");
        signup.setName("Alice");
        signup.setPassword("secret123");
        signup.setRoles(List.of());
        assertThatThrownBy(() -> service.createUser(signup)).isInstanceOf(DuplicateException.class);
    }

    @Test
    void patchUpdatesName() {
        UserEntity alice = activeUser("alice@example.com").setName("Alice");
        when(repository.findById(1L)).thenReturn(Optional.of(alice));
        when(repository.findByEmailIgnoreCaseAndIdNot(any(), any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserDTO patch = new UserDTO().setId(1L).setEmail("alice@example.com").setName("  Alice Johnson ");
        assertThat(service.patchUser(patch, 1L).getName()).isEqualTo("Alice Johnson");
    }

    @Test
    void deletingUserRevokesTokensByEmail() {
        when(repository.findById(1L)).thenReturn(Optional.of(activeUser("alice@example.com")));
        service.deleteById(1L);
        verify(revocation).revokeAllTokensOf("alice@example.com");
    }

    private static UserEntity activeUser(String email) {
        return new UserEntity().setEmail(email).setName("Alice").setEnabled(true)
                .setAccountNonExpired(true).setAccountNonLocked(true).setCredentialsNonExpired(true);
    }
}
