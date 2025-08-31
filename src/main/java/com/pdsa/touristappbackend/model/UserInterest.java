package com.pdsa.touristappbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
