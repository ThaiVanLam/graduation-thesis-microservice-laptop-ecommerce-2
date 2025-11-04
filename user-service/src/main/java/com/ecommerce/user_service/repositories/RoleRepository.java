package com.ecommerce.user_service.repositories;


import com.ecommerce.user_service.model.AppRole;
import com.ecommerce.user_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRole appRole);
}
