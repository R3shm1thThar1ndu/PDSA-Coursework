package com.pdsa.touristappbackend.repository;

import com.pdsa.touristappbackend.model.UserInterest;
import com.pdsa.touristappbackend.model.UserInterestId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, UserInterestId> {
    List<UserInterest> findByUserId(Long userId);
}
