package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.model.Users;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtils;

import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {

        Users registeredUser = userService.registerUser(request);
        UserResponse userResponse = new UserResponse();

        BeanUtils.copyProperties(registeredUser, userResponse);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody UserLoginRequest request) {

        Users authenticatedUser = userService.authenticateUser(request);
        userService.authenticateUser(request);

        String jwtToken = jwtUtils.generateToken(authenticatedUser);

        return new ResponseEntity<>(jwtToken, HttpStatus.OK);
    }

}
