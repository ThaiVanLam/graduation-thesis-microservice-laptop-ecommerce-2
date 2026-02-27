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

    private final String jwtSecret; //Secret key để verify token

    public JwtService(@Value("${spring.app.jwtSecret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

//    giải mã JWT token và lấy ra các claims(thông tin user)
//    claims thường chưa username, email, roles, expiration
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getKey()) //Verify signature
                .build()
                .parseSignedClaims(token)
                .getPayload(); //lấy payload(claim)
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token); //nếu parse thành công = token valid
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            log.warn("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }

    public List<String> extractRoles(Claims claims) {
        Object roles = claims.get("roles");

//        trường hợp 1: roles là list
        if (roles instanceof List<?>) {
            return ((List<?>) roles).stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .toList();
        }
//        trường hợp 2: roles là String(comma-separated)
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


//Tạo secret key từ base64 string để verify JWT signature
    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
