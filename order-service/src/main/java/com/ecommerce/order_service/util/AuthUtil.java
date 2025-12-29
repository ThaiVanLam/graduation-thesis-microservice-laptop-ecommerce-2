package com.ecommerce.order_service.util;


import com.ecommerce.order_service.exceptions.APIException;
import org.springframework.stereotype.Component;
import com.ecommerce.order_service.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthUtil {

    private static final String JWT_CLAIMS_ATTRIBUTE = "ORDER_SERVICE_JWT_CLAIMS";

    private final JwtService jwtService;

    public AuthUtil(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String loggedInEmail() {
        Claims claims = currentClaims();
        String email = jwtService.extractEmail(claims);
        if (email == null || email.isBlank()) {
            email = jwtService.extractUsername(claims);
        }
        if (email == null || email.isBlank()) {
            throw new APIException("Missing authenticated user email");
        }
        return email;
    }

    private Claims currentClaims() {
        HttpServletRequest request = currentRequest();
        Object cachedClaims = request.getAttribute(JWT_CLAIMS_ATTRIBUTE);
        if (cachedClaims instanceof Claims claims) {
            return claims;
        }

        String token = jwtService.resolveToken(request);
        if (token == null || token.isBlank()) {
            throw new APIException("Missing authentication token");
        }
        if (!jwtService.isTokenValid(token)) {
            throw new APIException("Invalid or expired authentication token");
        }

        Claims claims = jwtService.parseClaims(token);
        request.setAttribute(JWT_CLAIMS_ATTRIBUTE, claims);
        return claims;
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            throw new APIException("No active request context");
        }
        return servletAttributes.getRequest();
    }
}
