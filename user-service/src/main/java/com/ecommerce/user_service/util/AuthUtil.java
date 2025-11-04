package com.ecommerce.user_service.util;

import com.ecommerce.user_service.model.User;
import com.ecommerce.user_service.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthUtil {
    private static final String USER_HEADER = "X-Auth-User";
    private static final String EMAIL_HEADER = "X-Auth-Email";
    private static final String USER_ID_HEADER = "X-Auth-UserId";

    @Autowired
    private UserRepository userRepository;

    public String loggedInEmail() {
        HttpServletRequest request = currentRequest();
        String email = request.getHeader(EMAIL_HEADER);
        if (email != null && !email.isBlank()) {
            return email;
        }
        return loggedInUser().getEmail();
    }

    public Long loggedInUserId() {
        HttpServletRequest request = currentRequest();
        String userId = request.getHeader(USER_ID_HEADER);
        if (userId != null && !userId.isBlank()) {
            try {
                return Long.parseLong(userId);
            } catch (NumberFormatException ignored) {
                // fallback to lookup
            }
        }
        return loggedInUser().getUserId();
    }

    public User loggedInUser() {
        HttpServletRequest request = currentRequest();
        String username = request.getHeader(USER_HEADER);
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("Missing authenticated user information");
        }
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            throw new UsernameNotFoundException("No active request context");
        }
        return servletAttributes.getRequest();
    }
}
