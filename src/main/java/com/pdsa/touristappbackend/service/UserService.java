package com.pdsa.touristappbackend.service;

import com.pdsa.touristappbackend.model.Interest;
import com.pdsa.touristappbackend.model.User;
import com.pdsa.touristappbackend.model.UserRegisterRequest;
import com.pdsa.touristappbackend.repository.InterestRepository;
import com.pdsa.touristappbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service class for user-related operations such as registration and login.
 * Handles user creation, interest association, and credential validation.
 * Uses UserRepository and InterestRepository for database interactions.
 * Throws RuntimeException for error cases like existing usernames or invalid credentials.
 * Methods:
 * - register(UserRegisterRequest request): Registers a new user with interests.
 * - login(String username, String password): Validates user credentials and returns the user.
 * @see UserRepository
 * @see InterestRepository
 * @see User
 * @see Interest
 * @see UserRegisterRequest
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestRepository interestRepository;

    public User register(UserRegisterRequest request) {
        // Check if username exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        Set<Interest> userInterests = new HashSet<>();
        for (String interestName : request.getInterests()) {
            Interest interest = interestRepository.findByName(interestName)
                    .orElseThrow(() -> new RuntimeException("Invalid interest: " + interestName));
            userInterests.add(interest);
        }

        user.setInterests(userInterests);

        return userRepository.save(user);
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }
}
