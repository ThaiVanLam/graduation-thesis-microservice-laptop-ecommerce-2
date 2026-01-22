package com.ecommerce.api_gateway.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

//map các properties từ application.yaml có prefix gateway.security
@ConfigurationProperties(prefix = "gateway.security")
public class GatewaySecurityProperties {
//    lưu danh sách các đường dẫn công khai(không cần authentication)
//    sử dụng trong AuthenticationFilter để kiểm tra xem request có cần xác thực không
    private final List<String> publicPaths = new ArrayList<>();
    private final List<RoleMapping> roleMappings = new ArrayList<>();

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public List<RoleMapping> getRoleMappings() {
        return roleMappings;
    }

    public static class RoleMapping {
        private String pattern; //Pattern của URL
        private List<String> roles = new ArrayList<>(); //Các role được phép truy cập

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
