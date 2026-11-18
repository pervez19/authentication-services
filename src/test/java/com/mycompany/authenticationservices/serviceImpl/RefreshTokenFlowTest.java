package com.mycompany.authenticationservices.serviceImpl;

import com.mycompany.authenticationservices.dto.LoginDTO;
import com.mycompany.authenticationservices.dto.LoginResponseDTO;
import com.mycompany.authenticationservices.entity.UserEntity;
import com.mycompany.authenticationservices.security.JwtTokenService;
import com.mycompany.authenticationservices.security.JwtTokenServiceTest;
import com.mycompany.authenticationservices.security.TokenRevocationService;
import com.mycompany.authenticationservices.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RefreshTokenFlowTest {

    private final JwtTokenService jwt = new JwtTokenService(JwtTokenServiceTest.config(JwtTokenServiceTest.rsaKeyPair()));
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final TokenRevocationService revocation = mock(TokenRevocationService.class);
    private final UserService userService = mock(UserService.class);
    private final UserEntity alice = new UserEntity().setEmail("alice@example.com").setName("Alice Smith");
    private final AuthenticationServiceImpl service = new AuthenticationServiceImpl(
            authenticationManager, jwt, revocation, userService, null, null, null, null);

    @BeforeEach
    void setUp() {
        when(userService.loadUserByUsername("alice@example.com")).thenReturn(alice);
    }

    @Test
    void signinReturnsAccessAndRefreshToken() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(alice, null, alice.getAuthorities()));
        LoginResponseDTO response = service.authenticateUser(new LoginDTO("alice@example.com", "secret123"));
        assertThat(jwt.parseAccessToken(response.getToken()).getSubject()).isEqualTo("alice@example.com");
        assertThat(jwt.parseRefreshToken(response.getRefreshToken()).getSubject()).isEqualTo("alice@example.com");
        assertThat(response.getRefreshTokenDuration()).isEqualTo(120_000);
    }

    @Test
    void refreshIssuesNewPairAndConsumesOldRefreshToken() {
        String refresh = refreshTokenFor(alice);
        LoginResponseDTO response = service.refreshToken(refresh);

        assertThat(jwt.parseAccessToken(response.getToken()).getSubject()).isEqualTo("alice@example.com");
        Claims newRefresh = jwt.parseRefreshToken(response.getRefreshToken());
        assertThat(newRefresh.getId()).isNotEqualTo(jwt.parseRefreshToken(refresh).getId());
        verify(revocation).markUsed(argThat(c -> c.getId().equals(jwt.parseRefreshToken(refresh).getId())));
    }

    @Test
    void replayedRefreshTokenRevokesAllSessions() {
        when(revocation.markUsed(any())).thenReturn(TokenRevocationService.USED);
        assertThatThrownBy(() -> service.refreshToken(refreshTokenFor(alice))).isInstanceOf(JwtException.class);
        verify(revocation).revokeAllTokensOf("alice@example.com");
    }

    @Test
    void loggedOutRefreshTokenIsRejected() {
        when(revocation.markUsed(any())).thenReturn(TokenRevocationService.REVOKED);
        assertThatThrownBy(() -> service.refreshToken(refreshTokenFor(alice))).isInstanceOf(JwtException.class);
        verify(revocation, never()).revokeAllTokensOf(any());
    }

    @Test
    void refreshTokenOfRevokedUserIsRejected() {
        when(revocation.isRevokedForUser(any())).thenReturn(true);
        assertThatThrownBy(() -> service.refreshToken(refreshTokenFor(alice))).isInstanceOf(JwtException.class);
        verify(revocation, never()).markUsed(any());
    }

    @Test
    void accessTokenCannotBeUsedToRefresh() {
        String access = jwt.doGenerateToken(new UsernamePasswordAuthenticationToken(alice, null));
        assertThatThrownBy(() -> service.refreshToken(access)).isInstanceOf(JwtException.class);
    }

    @Test
    void logoutRevokesAccessAndRefreshToken() {
        String access = jwt.doGenerateToken(new UsernamePasswordAuthenticationToken(alice, null));
        service.logout(access, refreshTokenFor(alice));
        verify(revocation, times(2)).revokeToken(any());
    }

    private String refreshTokenFor(UserEntity user) {
        return jwt.doGenerateRefreshToken(new UsernamePasswordAuthenticationToken(user, null));
    }
}
