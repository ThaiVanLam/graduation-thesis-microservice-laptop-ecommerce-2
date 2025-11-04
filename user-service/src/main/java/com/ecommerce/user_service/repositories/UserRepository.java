package com.ecommerce.user_service.repositories;


import com.ecommerce.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userame);


    boolean existsByEmail(String email);

    boolean existsByUserName(String user1);
}
