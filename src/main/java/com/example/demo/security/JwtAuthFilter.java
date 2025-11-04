package com.example.demo.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.model.Users;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        if(authHeader != null && authHeader.startsWith("Bearer")){
            jwtToken = authHeader.substring(7);
            try {
                username = jwtUtils.extractUsername(jwtToken);
            } catch (Exception e) {
                logger.warn("JWT extraction failed: " + e.getMessage());
            }
        }


        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            Users foundUser = userService.findByUsername(username);

            User user = new User(foundUser.getUsername(), foundUser.getPasswordHash(), Collections.emptyList());

            if(jwtUtils.validateToken(jwtToken)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwtToken, user, user.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
