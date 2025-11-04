package com.ecommerce.api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final String jwtSecret;

    public JwtService(@Value("${spring.app.jwtSecret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            log.warn("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }

    public List<String> extractRoles(Claims claims) {
        Object roles = claims.get("roles");
        if (roles instanceof List<?>) {
            return ((List<?>) roles).stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .toList();
        }
        if (roles instanceof String rolesString) {
            if (rolesString.isBlank()) {
                return List.of();
            }
            return List.of(rolesString.split(","))
                    .stream()
                    .map(String::trim)
                    .filter(role -> !role.isEmpty())
                    .toList();
        }
        return Collections.emptyList();
    }

    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    public String extractEmail(Claims claims) {
        Object email = claims.get("email");
        return email != null ? email.toString() : null;
    }

    public String extractUserId(Claims claims) {
        Object userId = claims.get("userId");
        return userId != null ? userId.toString() : null;
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
