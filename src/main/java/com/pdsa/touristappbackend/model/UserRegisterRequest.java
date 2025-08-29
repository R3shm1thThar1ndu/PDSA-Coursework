package com.pdsa.touristappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@AllArgsConstructor
@Getter
public class UserRegisterRequest {
    private String username;
    private String password;
    private List<String> interests;
}
