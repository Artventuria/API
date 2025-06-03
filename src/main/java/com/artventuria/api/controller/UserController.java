package com.artventuria.api.controller;

import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.domain.postgresql.Collection;
import com.artventuria.api.dto.user.UpdateUserRequest;
import com.artventuria.api.service.badge.BadgeProgressService;
import com.artventuria.api.service.collection.CollectionService;
import com.artventuria.api.service.user.impl.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.artventuria.api.dto.auth.ExtendedUserResponse;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userService;
    private final BadgeProgressService badgeProgressService;
    private final CollectionService collectionService;

    @Autowired
    public UserController(UserServiceImpl userService, BadgeProgressService badgeProgressService, CollectionService collectionService) {
        this.userService = userService;
        this.badgeProgressService = badgeProgressService;
        this.collectionService = collectionService;
    }

    @GetMapping("/me")
    public ResponseEntity<ExtendedUserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userService.getCurrentUser(email);
        
        // Calculate badge count - number of completed badges for the user
        int badgeCount = badgeProgressService.getCompletedBadges(user.getId()).size();
        
        // Calculate total artworks in user's collections
        List<Collection> userCollections = collectionService.getUserCollections(user.getId(), 1000, 0);
        int artworkCount = 0;
        for (Collection collection : userCollections) {
            artworkCount += collectionService.getCollectionArtworksCount(collection.getId());
        }
        
        ExtendedUserResponse response = new ExtendedUserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getPoints(), user.getLastLogin(), user.getCreatedAt(), user.getUpdatedAt(),
                badgeCount, artworkCount);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<ExtendedUserResponse> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User updatedUser = userService.updateUser(email, request.getUsername(), request.getEmail());

        // Calculate badge count - number of completed badges for the user
        int badgeCount = badgeProgressService.getCompletedBadges(updatedUser.getId()).size();
        
        // Calculate total artworks in user's collections
        List<Collection> userCollections = collectionService.getUserCollections(updatedUser.getId(), 1000, 0);
        int artworkCount = 0;
        for (Collection collection : userCollections) {
            artworkCount += collectionService.getCollectionArtworksCount(collection.getId());
        }
        
        ExtendedUserResponse response = new ExtendedUserResponse(updatedUser.getId(), updatedUser.getUsername(),
                updatedUser.getEmail(), updatedUser.getPoints(), updatedUser.getLastLogin(), 
                updatedUser.getCreatedAt(), updatedUser.getUpdatedAt(), badgeCount, artworkCount);
        return ResponseEntity.ok(response);
    }
}