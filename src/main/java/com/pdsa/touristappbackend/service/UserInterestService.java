package com.pdsa.touristappbackend.service;

import com.pdsa.touristappbackend.model.User;
import com.pdsa.touristappbackend.model.UserInterest;
import com.pdsa.touristappbackend.repository.UserInterestRepository;
import com.pdsa.touristappbackend.repository.InterestRepository;
import com.pdsa.touristappbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing user interests and calculating their weights.
 * This class interacts with UserInterestRepository, InterestRepository, and UserRepository
 * to fetch and process user interest data.
 * The main functionality includes retrieving a map of user interests with associated weights.
 * The weight is currently set to 1 for each interest, but this can be extended in the future.
 * The service throws a RuntimeException if the user is not found.
 */
@Service
public class UserInterestService {

    private final UserInterestRepository userInterestRepo;
    private final InterestRepository interestRepo;
    private final UserRepository userRepo;

    public UserInterestService(UserInterestRepository userInterestRepo,
                               InterestRepository interestRepo,
                               UserRepository userRepo) {
        this.userInterestRepo = userInterestRepo;
        this.interestRepo = interestRepo;
        this.userRepo = userRepo;
    }


    public Map<String, Integer> getWeightedUserInterests(String username) {
        Map<String, Integer> weights = new HashMap<>();

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<UserInterest> uiList = userInterestRepo.findByUserId(user.getId());

        for (UserInterest ui : uiList) {
            // interestId -> interest name
            var interest = interestRepo.findById(ui.getInterestId())
                    .orElse(null);

            if (interest != null) {
                // For now weight = 1 (you can extend later with rating column)
                weights.put(interest.getName(), 1);
            }
        }

        return weights;
    }
}
