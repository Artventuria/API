package com.artventuria.api.controller;

import com.artventuria.api.domain.postgresql.UserProfile;
import com.artventuria.api.service.user.UserProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Integer userId) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfile> updateUserProfile(
            @PathVariable Integer userId,
            @RequestBody UserProfile profile) {
        return ResponseEntity.ok(userProfileService.updateUserProfile(userId, profile));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Integer userId) {
        userProfileService.deleteUserProfile(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/bio")
    public ResponseEntity<Void> updateBio(
            @PathVariable Integer userId,
            @RequestParam String bio) {
        userProfileService.updateBio(userId, bio);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/location")
    public ResponseEntity<Void> updateLocation(
            @PathVariable Integer userId,
            @RequestParam String location) {
        userProfileService.updateLocation(userId, location);
        return ResponseEntity.ok().build();
    }
}