package com.pdsa.touristappbackend.repository;

import com.pdsa.touristappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repository interface for User entity with method to find by username
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

