package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequest{
    
    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank
    @Email(message = "Email must be valid.")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(max = 50, message = "Password cannot exceed 50 characters")
    private String password;

    @NotBlank(message = "First name cannot be empty.")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Middle name cannot exceed 50 characters")
    private String middleName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @Size(max = 255, message = "Display picture url cannot exceed 255 characters.")
    private String displayPictureUrl;

}