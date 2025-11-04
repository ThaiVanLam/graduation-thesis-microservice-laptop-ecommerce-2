package com.ecommerce.order_service.util;


import com.ecommerce.order_service.exceptions.APIException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthUtil {

    private static final String EMAIL_HEADER = "X-Auth-Email";
    private static final String USER_HEADER = "X-Auth-User";

    public String loggedInEmail() {
        HttpServletRequest request = currentRequest();
        String email = request.getHeader(EMAIL_HEADER);
        if ((email == null || email.isBlank())) {
            email = request.getHeader(USER_HEADER);
        }
        if (email == null || email.isBlank()) {
            throw new APIException("Missing authenticated user email");
        }
        return email;
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            throw new APIException("No active request context");
        }
        return servletAttributes.getRequest();
    }
}
