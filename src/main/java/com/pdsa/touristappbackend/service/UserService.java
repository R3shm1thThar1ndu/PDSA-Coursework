package com.pdsa.touristappbackend.service;

import com.pdsa.touristappbackend.model.Interest;
import com.pdsa.touristappbackend.model.User;
import com.pdsa.touristappbackend.model.UserLoginRequest;
import com.pdsa.touristappbackend.model.UserRegisterRequest;
import com.pdsa.touristappbackend.repository.InterestRepository;
import com.pdsa.touristappbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestRepository interestRepository;

    public User register(UserRegisterRequest request){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        Set<Interest> userInterests = new HashSet<>();
        for (String interestName : request.getInterests()) {
            Interest interest = interestRepository.findByName(interestName)
                    .orElseThrow(() -> new RuntimeException(
                            "Interest '" + interestName + "' does not exist in the database"));
            userInterests.add(interest);
        }

        user.setInterests(userInterests);

        return userRepository.save(user);
    }

    public User login(String username, String password){
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
    }
}
