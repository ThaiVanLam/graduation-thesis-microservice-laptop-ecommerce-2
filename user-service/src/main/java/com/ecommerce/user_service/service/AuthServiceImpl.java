package com.ecommerce.user_service.service;

import com.ecommerce.user_service.model.AppRole;
import com.ecommerce.user_service.model.Role;
import com.ecommerce.user_service.model.User;
import com.ecommerce.user_service.payload.AuthenticationResult;
import com.ecommerce.user_service.repositories.RoleRepository;
import com.ecommerce.user_service.repositories.UserRepository;
import com.ecommerce.user_service.security.jwt.JwtUtils;
import com.ecommerce.user_service.security.request.LoginRequest;
import com.ecommerce.user_service.security.request.SignupRequest;
import com.ecommerce.user_service.security.response.MessageResponse;
import com.ecommerce.user_service.security.response.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public AuthenticationResult login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByUserName(loginRequest.getUsername());

        if (optionalUser.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), optionalUser.get().getPassword())) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();
        String token = jwtUtils.generateToken(user);
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(token);
        List<String> roles = user.getRoles().stream().map(role -> role.getRoleName().name()).collect(Collectors.toList());
        UserInfoResponse loginResponse = new UserInfoResponse(user.getUserId(), token, user.getUserName(), user.getEmail(), roles);
        return new AuthenticationResult(loginResponse, jwtCookie);
    }

    @Override
    public ResponseEntity<MessageResponse> register(SignupRequest signupRequest) {
        if (userRepository.existsByUserName(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
        }

        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @Override
    public UserInfoResponse getCurrentUserDetails(HttpServletRequest httpServletRequest) {
        String jwt = jwtUtils.getJwtFromCookies(httpServletRequest);
        if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid or missing authentication token"));
        }
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toList());
        UserInfoResponse response = new UserInfoResponse(user.getUserId(), user.getUserName(), roles);
    }

    @Override
    public ResponseCookie logoutUser() {
        return jwtUtils.getCleanJwtCookie();
    }

    @Override
    public String getUsername(HttpServletRequest httpServletRequest) {
        String jwt = jwtUtils.getJwtFromCookies(httpServletRequest);
        if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
            System.out.println(jwt);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid or missing authentication token"));
        }
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        return username;
    }
}
