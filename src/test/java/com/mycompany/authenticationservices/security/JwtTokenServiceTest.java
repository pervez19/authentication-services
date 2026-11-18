package com.mycompany.authenticationservices.security;

import com.mycompany.authenticationservices.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.KeyPair;
import java.time.Duration;
import java.security.KeyPairGenerator;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtTokenServiceTest {

    private final KeyPair keys = rsaKeyPair();
    private final JwtConfig config = config(keys);
    private final JwtTokenService service = new JwtTokenService(config);

    @Test
    void signsWithPrivateKeyAndVerifiesWithPublicKey() {
        Claims claims = service.parseAccessToken(service.doGenerateToken(login("alice@example.com")));
        assertThat(claims.getSubject()).isEqualTo("alice@example.com");
        assertThat(claims.getId()).isNotBlank();
        assertThat(claims.getIssuedAt()).isNotNull();
    }

    @Test
    void rejectsTokenSignedWithAnotherKey() {
        String forged = Jwts.builder().setSubject("mallory").setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(rsaKeyPair().getPrivate(), SignatureAlgorithm.RS256).compact();
        assertThatThrownBy(() -> service.parseAccessToken(forged)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsOldSharedSecretHs512Token() {
        String hmac = Jwts.builder().setSubject("alice").setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS512)).compact();
        assertThatThrownBy(() -> service.parseAccessToken(hmac)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsRefreshTokenUsedAsAccessToken() {
        String refresh = service.doGenerateRefreshToken(login("alice@example.com"));
        assertThatThrownBy(() -> service.parseAccessToken(refresh)).isInstanceOf(JwtException.class);
    }

    @Test
    void resolvesBearerToken() {
        assertThat(service.resolveToken("Bearer abc")).contains("abc");
        assertThat(service.resolveToken("Basic abc")).isEmpty();
        assertThat(service.resolveToken(null)).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    void revocationRules() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        ValueOperations<String, String> ops = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(ops);
        TokenRevocationService revocation = new TokenRevocationService(redis, config);
        Claims claims = service.parseAccessToken(service.doGenerateToken(login("alice@example.com")));
        long iat = claims.getIssuedAt().toInstant().getEpochSecond();

        when(ops.multiGet(anyList())).thenReturn(Arrays.asList(null, null));
        assertThat(revocation.isRevoked(claims)).isFalse();

        when(ops.multiGet(anyList())).thenReturn(Arrays.asList("1", null)); // logged out
        assertThat(revocation.isRevoked(claims)).isTrue();

        when(ops.multiGet(anyList())).thenReturn(Arrays.asList(null, String.valueOf(iat))); // user deleted after issue
        assertThat(revocation.isRevoked(claims)).isTrue();

        when(ops.multiGet(anyList())).thenReturn(Arrays.asList(null, String.valueOf(iat - 10))); // token issued after
        assertThat(revocation.isRevoked(claims)).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    void markUsedIsAtomicFirstUseWins() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        ValueOperations<String, String> ops = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(ops);
        TokenRevocationService revocation = new TokenRevocationService(redis, config);
        Claims refresh = service.parseRefreshToken(service.doGenerateRefreshToken(login("alice@example.com")));
        String key = TokenRevocationService.REVOKED_TOKEN_KEY + refresh.getId();

        when(ops.setIfAbsent(eq(key), eq(TokenRevocationService.USED), any(Duration.class))).thenReturn(true);
        assertThat(revocation.markUsed(refresh)).isNull(); // first use

        when(ops.setIfAbsent(eq(key), eq(TokenRevocationService.USED), any(Duration.class))).thenReturn(false);
        when(ops.get(key)).thenReturn(TokenRevocationService.USED);
        assertThat(revocation.markUsed(refresh)).isEqualTo(TokenRevocationService.USED); // replay

        when(ops.get(key)).thenReturn(TokenRevocationService.REVOKED);
        assertThat(revocation.markUsed(refresh)).isEqualTo(TokenRevocationService.REVOKED); // logged out
    }

    @Test
    void parseRefreshTokenRejectsAccessToken() {
        String access = service.doGenerateToken(login("alice@example.com"));
        assertThatThrownBy(() -> service.parseRefreshToken(access)).isInstanceOf(JwtException.class);
    }

    private static UsernamePasswordAuthenticationToken login(String username) {
        return new UsernamePasswordAuthenticationToken(new UserEntity().setEmail(username), null);
    }

    public static JwtConfig config(KeyPair keys) {
        try {
            Base64.Encoder b64 = Base64.getEncoder();
            JwtConfig c = new JwtConfig(b64.encodeToString(keys.getPrivate().getEncoded()),
                    b64.encodeToString(keys.getPublic().getEncoded()));
            c.setPrefix("Bearer");
            c.setExpiration(60_000);
            c.setRefreshExpiration(120_000);
            return c;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static KeyPair rsaKeyPair() {
        try {
            KeyPairGenerator g = KeyPairGenerator.getInstance("RSA");
            g.initialize(2048);
            return g.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
