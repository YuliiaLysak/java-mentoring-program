package edu.lysak.reactPractice.hello.config;

import edu.lysak.reactPractice.hello.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expirationTime;

    public Optional<String> extractUsername(String authToken) {
        Optional<Claims> claims = getClaimsFromToken(authToken);
        return claims.map(Claims::getSubject);
    }

    public boolean isValidToken(String authToken) {
        Optional<Claims> claims = getClaimsFromToken(authToken);
        return claims
                .map(value -> value
                        .getExpiration()
                        .after(new Date())
                )
                .orElse(false);
    }

    public List<SimpleGrantedAuthority> getRoles(String authToken) {
        Optional<Claims> claims = getClaimsFromToken(authToken);
        if (claims.isEmpty()) {
            return List.of();
        }

        List<String> roles = claims.get().get("role", List.class);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", List.of(user.getRole()));
        long expirationSeconds = Long.parseLong(expirationTime);
        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime() + TimeUnit.SECONDS.toMillis(expirationSeconds));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private Optional<Claims> getClaimsFromToken(String authToken) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        try {
            return Optional.of(Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody());
        } catch (Exception exception) {
            log.error("Problems with authToken parsing due to exception {}", exception.getMessage(), exception);
            return Optional.empty();
        }
    }
}
