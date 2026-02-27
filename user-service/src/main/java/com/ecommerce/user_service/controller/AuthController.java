package com.ecommerce.user_service.controller;


import com.ecommerce.user_service.config.AppConstants;
import com.ecommerce.user_service.payload.AuthenticationResult;
import com.ecommerce.user_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import com.ecommerce.user_service.security.jwt.JwtUtils;
import com.ecommerce.user_service.security.request.LoginRequest;
import com.ecommerce.user_service.security.request.SignupRequest;
import com.ecommerce.user_service.security.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
        return ResponseEntity.ok(authService.getCurrentUserDetails(httpServletRequest));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser() {
        ResponseCookie cookie = authService.logoutUser();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("You've been signed out!"));
    }

    @GetMapping("/sellers")
    public ResponseEntity<?> getAllSellers(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber) {

        Sort sortByAndOrder = Sort.by(AppConstants.SORT_USERS_BY).descending();
        Pageable pageDetails = PageRequest.of(pageNumber ,
                Integer.parseInt(AppConstants.PAGE_SIZE), sortByAndOrder);

        return ResponseEntity.ok(authService.getAllSellers(pageDetails));
    }

    @GetMapping("/customers")
    public ResponseEntity<?> getAllCustomers(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber) {

        Sort sortByAndOrder = Sort.by(AppConstants.SORT_USERS_BY).descending();
        Pageable pageDetails = PageRequest.of(pageNumber ,
                Integer.parseInt(AppConstants.PAGE_SIZE), sortByAndOrder);

        return ResponseEntity.ok(authService.getAllCustomers(pageDetails));
    }

    @DeleteMapping("/customers/{userId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.deleteCustomer(userId));
    }

    @DeleteMapping("/sellers/{userId}")
    public ResponseEntity<?> deleteSeller(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.deleteSeller(userId));
    }
}
