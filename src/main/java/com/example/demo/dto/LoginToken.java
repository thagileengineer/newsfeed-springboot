package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginToken{
    @NotEmpty
    private final String token;
    
    public LoginToken(String token){
        this.token = token;
    }
}