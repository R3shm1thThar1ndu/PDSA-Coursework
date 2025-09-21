package com.pdsa.touristappbackend.repository;

import com.pdsa.touristappbackend.model.UserInterest;
import com.pdsa.touristappbackend.model.UserInterestId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository interface for UserInterest entity with method to find interests by user ID
public interface UserInterestRepository extends JpaRepository<UserInterest, UserInterestId> {
    List<UserInterest> findByUserId(Long userId);
}
