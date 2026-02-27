package com.ecommerce.user_service.security.jwt;

import com.ecommerce.user_service.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import java.util.Collections;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.ecom.app.jwtCookieName}")
    private String jwtCookie;

    public String getJwtFromCookies(HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

//    tạo cookie chứa JWT
    public ResponseCookie generateJwtCookie(String token) {
        return ResponseCookie.from(jwtCookie, token)
                .path("/")
                .maxAge(24 * 60 * 60)
                .httpOnly(false)
                .secure(false)
                .build();
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null)
                .path("/")
                .build();
    }

//    Tạo JWT mới
    public String generateToken(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toList());
        return generateToken(user.getUserId(), user.getUserName(), user.getEmail(), roleNames);
    }

    public String generateToken(Long userId, String username, String email, Collection<String> roles) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtExpirationMs)))
                .signWith(key())
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public Claims getClaimsFromJwtToken(String authToken) {
        return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken).getPayload();
    }

    public String getUserNameFromJwtToken(String token) {
        return getClaimsFromJwtToken(token).getSubject();
    }

    public Long getUserIdFromJwtToken(String token) {
        Number userId = getClaimsFromJwtToken(token).get("userId", Number.class);
        return userId != null ? userId.longValue() : null;
    }

    public String getEmailFromJwtToken(String token) {
        return getClaimsFromJwtToken(token).get("email", String.class);
    }

    public List<String> getRolesFromJwtToken(String token) {
        List<?> roles = getClaimsFromJwtToken(token).get("roles", List.class);
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream().map(Object::toString).collect(Collectors.toList());
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
