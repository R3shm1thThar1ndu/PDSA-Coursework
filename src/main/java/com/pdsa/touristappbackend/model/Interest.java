package com.pdsa.touristappbackend.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * Interest entity representing a specific interest or hobby.
 * Each interest has a unique ID and a name.
 * This entity is used to categorize users based on their interests.
 */
@Entity
@Table(name = "interests")
@Getter
@Setter
@NoArgsConstructor
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    public Interest(String name) {
        this.name = name;
    }
}