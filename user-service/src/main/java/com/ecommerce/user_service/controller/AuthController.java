package com.ecommerce.user_service.controller;


import com.ecommerce.user_service.model.AppRole;
import com.ecommerce.user_service.model.Role;
import com.ecommerce.user_service.model.User;
import com.ecommerce.user_service.payload.AuthenticationResult;
import com.ecommerce.user_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import com.ecommerce.user_service.repositories.RoleRepository;
import com.ecommerce.user_service.repositories.UserRepository;
import com.ecommerce.user_service.security.jwt.JwtUtils;
import com.ecommerce.user_service.security.request.LoginRequest;
import com.ecommerce.user_service.security.request.SignupRequest;
import com.ecommerce.user_service.security.response.MessageResponse;
import com.ecommerce.user_service.security.response.UserInfoResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        AuthenticationResult result = authService.login(loginRequest);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, result.getJwtCookie().toString()).body(result.getResponse());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.register(signupRequest);
    }

    @GetMapping("/username")
    public ResponseEntity<?> currentUserName(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(authService.getUsername(httpServletRequest));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(authService.getCurrentUserDetails(HttpServletRequest httpServletRequest));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser() {
        ResponseCookie cookie = authService.logoutUser();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("You've been signed out!"));
    }
}
