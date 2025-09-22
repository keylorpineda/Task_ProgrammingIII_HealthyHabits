package task.healthyhabits.security.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMs;
    private final Clock clock;

    @Autowired
    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs
    ) {
        this(secret, expirationMs, Clock.systemUTC());
    }

    public JwtService(String secret, long expirationMs, Clock clock) {
        this.secret = secret;
        this.expirationMs = expirationMs;
        this.clock = clock == null ? Clock.systemUTC() : clock;
    }

    public String generateToken(String subject) {
        return generateToken(Map.of(), subject);
    }

    public String generateToken(Map<String, Object> extraClaims, String subject) {
        Instant issuedAtInstant = clock.instant();
        Date issuedAt = Date.from(issuedAtInstant);
        Date expiration = Date.from(issuedAtInstant.plusMillis(expirationMs));

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, String expectedUsername) {
        String username = extractUsername(token);
        return username != null
                && expectedUsername != null
                && username.equalsIgnoreCase(expectedUsername)
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
         Date exp = extractClaim(token, Claims::getExpiration);
        return exp == null || exp.before(Date.from(clock.instant()));
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return resolver.apply(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private Key getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (DecodingException | IllegalArgumentException e) {
            byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(raw);
        }
    }
}
