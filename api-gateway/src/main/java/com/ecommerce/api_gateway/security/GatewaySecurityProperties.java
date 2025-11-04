package com.ecommerce.api_gateway.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "gateway.security")
public class GatewaySecurityProperties {

    private final List<String> publicPaths = new ArrayList<>();
    private final List<RoleMapping> roleMappings = new ArrayList<>();

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public List<RoleMapping> getRoleMappings() {
        return roleMappings;
    }

    public static class RoleMapping {
        private String pattern;
        private List<String> roles = new ArrayList<>();

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
