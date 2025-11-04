package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.LoginToken;
import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.model.Users;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtils;

import jakarta.validation.Valid;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
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
    @PostMapping("bulk-register")
    public ResponseEntity<?> bulkRegisterUsersCSV(@RequestParam("file") MultipartFile file) {

        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("No file uploaded.");
        }

        List<String> results = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            CSVFormat format = CSVFormat.Builder.create()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .build();

            Iterable<CSVRecord> records = format.parse(reader);

            for(CSVRecord record: records){
                try {
                    UserRegisterRequest request = new UserRegisterRequest();
                    request.setUsername(record.get("username"));
                    request.setEmail(record.get("email"));
                    request.setFirstName(record.get("first_name"));
                    request.setPassword(record.get("password"));

                    userService.registerUser(request);
                    results.add(request.getUsername() + " registered successfully.");
                } catch (Exception e) {
                    results.add("Error for " + record.get("username") + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to process file: " + e.getMessage());
        }

        return ResponseEntity.ok(results);
    }

    @PostMapping("login")
    public ResponseEntity<LoginToken> loginUser(@Valid @RequestBody UserLoginRequest request) {

        Users authenticatedUser = userService.authenticateUser(request);
        userService.authenticateUser(request);

        String jwtToken = jwtUtils.generateToken(authenticatedUser);

        LoginToken loginToken = new LoginToken(jwtToken);

        return new ResponseEntity<>(loginToken, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        Users user = userService.findByUsername(username);
        
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setMiddleName(user.getMiddleName());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

}
