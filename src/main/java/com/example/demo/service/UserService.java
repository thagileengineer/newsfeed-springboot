package com.example.demo.service;


import java.util.Collections;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.exception.AuthenticationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Users;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Users registerUser(UserRegisterRequest request){

        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new IllegalStateException("Username is already taken: " + request.getUsername());
        }

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new IllegalStateException("Email is already registered: " + request.getEmail());
        }

        Users newUser = new Users();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setMiddleName(request.getMiddleName());
        newUser.setDisplayPictureUrl(request.getDisplayPictureUrl());

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        newUser.setPasswordHash(hashedPassword);

        return userRepository.save(newUser);

    }

    public Users findUserById(Long id){
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: "+ id));
    }

    public Users authenticateUser(UserLoginRequest request) {

        Users storedUser = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new AuthenticationException("Invalid username or password"));


        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), storedUser.getPasswordHash());

        if(!passwordMatches){
            throw new AuthenticationException("Invalid username or password.");
        }

        return storedUser;

    }

    public Users findByUsername(String username){
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Username is not found " + username));

        return user;
    }

}
