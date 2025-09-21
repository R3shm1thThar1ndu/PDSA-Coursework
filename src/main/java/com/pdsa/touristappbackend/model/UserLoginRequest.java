package com.pdsa.touristappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * UserLoginRequest class representing a user login request with username and password
 * Fields:
 * - username: the user's username
 * - password: the user's password
 * Used for user authentication
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserLoginRequest {
    private String username;
    private String password;
}