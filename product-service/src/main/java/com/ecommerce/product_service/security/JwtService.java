package com.ecommerce.product_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final String jwtSecret;
    private final String jwtCookieName;

    public JwtService(@Value("${spring.app.jwtSecret}") String jwtSecret,
                      @Value("${spring.ecom.app.jwtCookieName:springBootEcom}") String jwtCookieName) {
        this.jwtSecret = jwtSecret;
        this.jwtCookieName = jwtCookieName;
    }

    public String resolveToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(Objects::nonNull)
                .filter(cookie -> jwtCookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(token -> token != null && !token.isBlank())
                .findFirst()
                .orElse(null);
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

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(Claims claims) {
        Object email = claims.get("email");
        return email != null ? email.toString() : null;
    }

    public Long extractUserId(Claims claims) {
        Object userId = claims.get("userId");
        if (userId instanceof Number number) {
            return number.longValue();
        }
        if (userId != null) {
            try {
                return Long.parseLong(userId.toString());
            } catch (NumberFormatException ex) {
                log.warn("Unable to parse userId from JWT claims: {}", userId);
            }
        }
        return null;
    }

    public String extractUsername(Claims claims) {
        return claims.getSubject();
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
        return List.of();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}