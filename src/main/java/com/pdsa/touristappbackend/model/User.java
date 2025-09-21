package com.pdsa.touristappbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * User entity representing a user in the system.
 * It includes fields for id, username, password, and a set of interests.
 * The interests are represented as a many-to-many relationship with the Interest entity.
 * This entity is used for user authentication and interest management.
 */
@Entity
@Table(name="users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_interests",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="interest_id")
    )
    private Set<Interest> interests = new HashSet<>();
}
