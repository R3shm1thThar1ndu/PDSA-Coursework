package com.pdsa.touristappbackend.controller;

import com.pdsa.touristappbackend.model.User;
import com.pdsa.touristappbackend.model.UserLoginRequest;
import com.pdsa.touristappbackend.model.UserRegisterRequest;
import com.pdsa.touristappbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody UserLoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(user);
    }
}