package com.ecommerce.api_gateway.security;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final String HEADER_USER = "X-Auth-User";
    private static final String HEADER_EMAIL = "X-Auth-Email";
    private static final String HEADER_ROLES = "X-Auth-Roles";
    private static final String HEADER_USER_ID = "X-Auth-UserId";

    private final JwtService jwtService;
    private final GatewaySecurityProperties securityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthenticationFilter(JwtService jwtService, GatewaySecurityProperties securityProperties) {
        this.jwtService = jwtService;
        this.securityProperties = securityProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isPublicRoute(request.getPath().value()) || isPreFlight(request)) {
            return chain.filter(exchange);
        }

        String token = resolveToken(request);
        if (token == null || token.isBlank()) {
            return unauthorized(exchange, "Missing Authorization header");
        }

        if (!jwtService.isTokenValid(token)) {
            return unauthorized(exchange, "Invalid or expired token");
        }

        Claims claims = jwtService.parseClaims(token);
        List<String> roles = jwtService.extractRoles(claims);
        Set<String> requiredRoles = resolveRequiredRoles(request.getPath().value());

        if (!requiredRoles.isEmpty() && roles.stream().noneMatch(requiredRoles::contains)) {
            return forbidden(exchange, "Insufficient permissions");
        }

        String username = jwtService.extractUsername(claims);
        ServerHttpRequest mutatedRequest = request.mutate()
                .headers(headers -> {
                    headers.remove(HEADER_USER);
                    headers.remove(HEADER_EMAIL);
                    headers.remove(HEADER_ROLES);
                    headers.remove(HEADER_USER_ID);
                    headers.add(HEADER_USER, username == null ? "" : username);
                    String email = jwtService.extractEmail(claims);
                    if (email != null) {
                        headers.add(HEADER_EMAIL, email);
                    }
                    String userId = jwtService.extractUserId(claims);
                    if (userId != null) {
                        headers.add(HEADER_USER_ID, userId);
                    }
                    if (!roles.isEmpty()) {
                        headers.add(HEADER_ROLES, String.join(",", roles));
                    }
                })
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean isPreFlight(ServerHttpRequest request) {
        return "OPTIONS".equalsIgnoreCase(String.valueOf(request.getMethod()));
    }

    private boolean isPublicRoute(String path) {
        return securityProperties.getPublicPaths().stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Set<String> resolveRequiredRoles(String path) {
        return securityProperties.getRoleMappings().stream()
                .filter(mapping -> mapping.getPattern() != null && pathMatcher.match(mapping.getPattern(), path))
                .flatMap(mapping -> mapping.getRoles().stream())
                .collect(Collectors.toSet());
    }

    private String resolveToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, message);
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        return writeErrorResponse(exchange, HttpStatus.FORBIDDEN, message);
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] response = ("{\"error\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(response)));
    }
}
