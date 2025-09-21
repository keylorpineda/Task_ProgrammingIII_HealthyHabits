package task.healthyhabits.security.JWT;


import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Test;
import task.healthyhabits.security.JWT.JwtService;

class JwtServiceTest {

    private static final String RAW_SECRET = "super-secret-key-for-tests-1234567890";
    private static final String BASE64_SECRET = Base64.getEncoder().encodeToString(RAW_SECRET.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_MS = 2_000L;

    @Test
    void generateTokenIncludesClaimsAndTimestamps() {
        JwtService service = new JwtService(BASE64_SECRET, EXPIRATION_MS);
        Instant before = Instant.now();

        String token = service.generateToken(Map.of("role", "ADMIN"), "user@example.com");

        Instant after = Instant.now();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(BASE64_SECRET)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertThat(claims.getSubject()).isEqualTo("user@example.com");
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(claims.getIssuedAt().toInstant()).isBetween(before.minusMillis(5), after.plusMillis(5));
        Duration validity = Duration.between(claims.getIssuedAt().toInstant(), claims.getExpiration().toInstant());
        assertThat(validity.toMillis()).isEqualTo(EXPIRATION_MS);
    }

    @Test
    void extractUsernameAndExpirationReturnExpectedValues() {
        JwtService service = new JwtService(BASE64_SECRET, EXPIRATION_MS);
        String token = service.generateToken("person@example.com");

        assertThat(service.extractUsername(token)).isEqualTo("person@example.com");
        Instant expiration = service.extractExpiration(token).toInstant();
        assertThat(expiration).isAfter(Instant.now());
    }

    @Test
    void isTokenValidIgnoresUsernameCase() {
        JwtService service = new JwtService(BASE64_SECRET, EXPIRATION_MS);
        String token = service.generateToken("CaseUser");

        assertThat(service.isTokenValid(token, "caseuser")).isTrue();
    }

    @Test
    void isTokenValidReturnsFalseForDifferentUser() {
        JwtService service = new JwtService(BASE64_SECRET, EXPIRATION_MS);
        String token = service.generateToken("expected@example.com");

        assertThat(service.isTokenValid(token, "other@example.com")).isFalse();
    }

    @Test
    void isTokenValidReturnsFalseWhenExpired() {
        JwtService service = new JwtService(BASE64_SECRET, -1_000L);
        String token = service.generateToken("expired@example.com");

        assertThat(service.isTokenValid(token, "expired@example.com")).isFalse();
    }

    @Test
    void generateTokenWorksWithPlainSecret() {
        String plainSecret = "plain-text-secret-value-that-is-long-enough-123";
        JwtService service = new JwtService(plainSecret, EXPIRATION_MS);

        String token = service.generateToken("plain@example.com");

        assertThat(service.extractUsername(token)).isEqualTo("plain@example.com");
        assertThat(service.isTokenValid(token, "plain@example.com")).isTrue();
    }
}