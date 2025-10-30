package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String displayPictureUrl;
    private LocalDateTime createdAt;
}
