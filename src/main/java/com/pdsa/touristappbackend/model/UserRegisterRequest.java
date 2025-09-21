package com.pdsa.touristappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


/**
 * UserRegisterRequest class representing a user registration request with username, password, and interests
 * Fields:
 * - username: the user's username
 * - password: the user's password
 * - interests: a list of interests selected by the user
 * Used for user registration
 */
@AllArgsConstructor
@Getter
public class UserRegisterRequest {
    private String username;
    private String password;
    private List<String> interests;
}
