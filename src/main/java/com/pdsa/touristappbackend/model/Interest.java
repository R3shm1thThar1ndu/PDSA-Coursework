package com.pdsa.touristappbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name="interests")
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}

