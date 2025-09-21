package com.pdsa.touristappbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/**
 * UserInterest entity representing the association between a user and an interest.
 * It includes fields for userId, interestId, and rating.
 * This entity is used to store the interests of users along with their ratings.
 * The combination of userId and interestId serves as the composite primary key.
 * This entity is mapped to the "user_interests" table in the database.
 * It uses the UserInterestId class as the composite key class.
 * The rating field represents the user's rating for the specific interest.
 * This entity is essential for managing user interests and their preferences.
 * It is part of a many-to-many relationship between users and interests.
 * The entity is annotated with @Entity and @Table to specify the table name.
 * The fields are annotated with @Id and @Column to define the primary key and column mappings.
 * The class uses Lombok annotations @Getter and @Setter to generate getter and setter methods.
 */
@Getter
@Setter
@Entity
@Table(name = "user_interests")
@IdClass(UserInterestId.class)
public class UserInterest {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "interest_id")
    private Long interestId;

    @Column(name = "rating")
    private Integer rating;


}
