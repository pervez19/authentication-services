package com.mycompany.authenticationservices.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * JWT deny-list in Redis. The key format is a contract shared with gateway-services
 * (TokenRevocationService there reads the same keys):
 * <ul>
 *   <li>{@code jwt:revoked:<jti>} - a single token was revoked (logout); expires with the token.</li>
 *   <li>{@code jwt:revoked-before:<email>} - epoch seconds; every token of that user issued at or
 *   before it is revoked (user deleted/disabled); expires after the longest token lifetime.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class TokenRevocationService {
    public static final String REVOKED_TOKEN_KEY = "jwt:revoked:";
    public static final String REVOKED_BEFORE_KEY = "jwt:revoked-before:";

    private final StringRedisTemplate redis;
    private final JwtConfig jwtConfig;

    public static final String REVOKED = "revoked";
    public static final String USED = "used";

    public void revokeToken(Claims claims) {
        long ttlMs = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (claims.getId() != null && ttlMs > 0) {
            redis.opsForValue().set(REVOKED_TOKEN_KEY + claims.getId(), REVOKED, Duration.ofMillis(ttlMs));
        }
    }

    /**
     * Atomically marks a refresh token as used (rotation: each refresh token works once).
     *
     * @return {@code null} on first use; otherwise the existing mark ({@link #USED} = replayed, {@link #REVOKED} = logged out)
     */
    public String markUsed(Claims claims) {
        long ttlMs = Math.max(claims.getExpiration().getTime() - System.currentTimeMillis(), 1);
        String key = REVOKED_TOKEN_KEY + claims.getId();
        if (Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(key, USED, Duration.ofMillis(ttlMs)))) {
            return null;
        }
        String existing = redis.opsForValue().get(key);
        return existing != null ? existing : REVOKED;
    }

    /** True if all tokens of the subject issued at or before some moment were revoked (e.g. user deleted). */
    public boolean isRevokedForUser(Claims claims) {
        String revokedBefore = redis.opsForValue().get(REVOKED_BEFORE_KEY + claims.getSubject());
        return revokedBefore != null && claims.getIssuedAt() != null
                && claims.getIssuedAt().toInstant().getEpochSecond() <= Long.parseLong(revokedBefore);
    }

    public void revokeAllTokensOf(String email) {
        long maxLifetimeMs = Math.max(jwtConfig.getExpiration(), jwtConfig.getRefreshExpiration());
        redis.opsForValue().set(REVOKED_BEFORE_KEY + email,
                String.valueOf(Instant.now().getEpochSecond()), Duration.ofMillis(maxLifetimeMs));
    }

    public boolean isRevoked(Claims claims) {
        List<String> values = redis.opsForValue().multiGet(
                List.of(REVOKED_TOKEN_KEY + claims.getId(), REVOKED_BEFORE_KEY + claims.getSubject()));
        if (values == null) {
            return false;
        }
        String revokedBefore = values.get(1);
        return values.get(0) != null
                || (revokedBefore != null && claims.getIssuedAt() != null
                && claims.getIssuedAt().toInstant().getEpochSecond() <= Long.parseLong(revokedBefore));
    }
}
