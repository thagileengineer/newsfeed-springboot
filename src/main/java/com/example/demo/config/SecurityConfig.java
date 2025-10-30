package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    /**
     * Defines the PasswordEncoder bean to be used throughout the application.
     * We use BCryptPasswordEncoder, the industry standard for strong password hashing.
     * * By annotating this method with @Bean, Spring registers the returned 
     * BCryptPasswordEncoder instance in the Application Context.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

