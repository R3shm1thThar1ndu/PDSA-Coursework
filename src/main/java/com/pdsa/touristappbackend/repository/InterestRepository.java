package com.pdsa.touristappbackend.repository;

import com.pdsa.touristappbackend.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *  Repository interface for Interest entity with method to find by name
 */
@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {
    Optional<Interest> findByName(String name);
}