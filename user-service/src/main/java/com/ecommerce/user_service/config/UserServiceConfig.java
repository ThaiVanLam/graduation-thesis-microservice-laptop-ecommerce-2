package com.ecommerce.user_service.config;

import com.ecommerce.user_service.model.AppRole;
import com.ecommerce.user_service.model.Role;
import com.ecommerce.user_service.model.User;
import com.ecommerce.user_service.repositories.RoleRepository;
import com.ecommerce.user_service.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class UserServiceConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));
            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_SELLER)));
            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

            if (!userRepository.existsByUserName("user1")) {
                User user = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
                user.setRoles(Set.of(userRole));
                userRepository.save(user);
            }

            if (!userRepository.existsByUserName("user2")) {
                User user = new User("user2", "user2@example.com", passwordEncoder.encode("password1"));
                user.setRoles(Set.of(userRole));
                userRepository.save(user);
            }

            if (!userRepository.existsByUserName("seller1")) {
                User seller = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
                seller.setRoles(Set.of(sellerRole));
                userRepository.save(seller);
            }

            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                admin.setRoles(Set.of(userRole, sellerRole, adminRole));
                userRepository.save(admin);
            }
        };
    }
}
