package com.ecommerce.user_service.util;

import com.ecommerce.user_service.model.User;
import com.ecommerce.user_service.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.ecommerce.user_service.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthUtil {
    private static final String JWT_CLAIMS_ATTRIBUTE = "USER_SERVICE_JWT_CLAIMS";

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public AuthUtil(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public String loggedInEmail() {
        Claims claims = currentClaims();
        String email = claims.get("email", String.class);
        if (email != null && !email.isBlank()) {
            return email;
        }
        return loggedInUser().getEmail();
    }

    public Long loggedInUserId() {
        Claims claims = currentClaims();
        Number userId = claims.get("userId", Number.class);
        if (userId != null) {
            return userId.longValue();
        }
        return loggedInUser().getUserId();
    }

    public User loggedInUser() {
        String username = currentClaims().getSubject();
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("Missing authenticated user information");
        }
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    private Claims currentClaims() {
        HttpServletRequest request = currentRequest();
        Object cachedClaims = request.getAttribute(JWT_CLAIMS_ATTRIBUTE);
        if (cachedClaims instanceof Claims claims) {
            return claims;
        }

        String token = jwtUtils.getJwtFromCookies(request);
        if (token == null || token.isBlank()) {
            throw new UsernameNotFoundException("Missing authentication token");
        }
        if (!jwtUtils.validateJwtToken(token)) {
            throw new UsernameNotFoundException("Invalid or expired authentication token");
        }

        Claims claims = jwtUtils.getClaimsFromJwtToken(token);
        request.setAttribute(JWT_CLAIMS_ATTRIBUTE, claims);
        return claims;
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            throw new UsernameNotFoundException("No active request context");
        }
        return servletAttributes.getRequest();
    }
}
