package com.artventuria.api.controller;

import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.dto.user.UpdateUserRequest;
import com.artventuria.api.service.user.impl.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.artventuria.api.dto.auth.ExtendedUserResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ExtendedUserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userService.getCurrentUser(email);
        ExtendedUserResponse response = new ExtendedUserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getPoints(),
                user.getLastLogin(), user.getCreatedAt(), user.getUpdatedAt());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<ExtendedUserResponse> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User updatedUser = userService.updateUser(email, request.getUsername(), request.getEmail());

        ExtendedUserResponse response = new ExtendedUserResponse(updatedUser.getId(), updatedUser.getUsername(),
                updatedUser.getEmail(), updatedUser.getPoints(),
                updatedUser.getLastLogin(), updatedUser.getCreatedAt(), updatedUser.getUpdatedAt());
        return ResponseEntity.ok(response);
    }
}