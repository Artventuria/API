package com.artventuria.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;

import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.repository.jpa.user.UserRepository;
import com.artventuria.api.service.nfc.NFCService;
import com.artventuria.api.domain.mongodb.ScanLog;
import com.artventuria.api.domain.postgresql.ValidationRule;
import com.artventuria.api.domain.postgresql.NFCTag;
import com.artventuria.api.dto.nfc.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/nfc")
public class NFCController {

    private final NFCService nfcService;
    private final UserRepository userRepository;

    public NFCController(NFCService nfcService, UserRepository userRepository) {
        this.nfcService = nfcService;
        this.userRepository = userRepository;
    }

    @PostMapping("/scan")
    public ResponseEntity<NFCScanResponse> scanNFCTag(@Valid @RequestBody NFCScanRequest request) {
        // Retrieve the authenticated user via the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Retrieve the user from the database by their email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Set the user ID in the request
        request.setUserId(user.getId().longValue());
        System.out.println("Using authenticated user ID: " + user.getId() + " (" + user.getEmail() + ")");

        // Process the NFC scan
        NFCScanResponse response = nfcService.processNFCScan(request);
        System.out.println("Scan processed for userId: " + request.getUserId() + ", tokenNFC: " + request.getToken());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/scans")
    public ResponseEntity<List<ScanLog>> getUserScans(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(nfcService.getUserScans(userId, limit, offset));
    }

    @GetMapping("/artwork/{artworkId}/scans")
    public ResponseEntity<List<ScanLog>> getArtworkScans(@PathVariable Integer artworkId) {
        return ResponseEntity.ok(nfcService.getArtworkScans(artworkId));
    }

    @GetMapping("/scans/time-range")
    public ResponseEntity<List<ScanLog>> getScansByTimeRange(
            @RequestParam Instant startTime,
            @RequestParam Instant endTime) {
        return ResponseEntity.ok(nfcService.getScansByTimeRange(startTime, endTime));
    }

    @GetMapping("/scans")
    public ResponseEntity<List<ScanLog>> getAllScans(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(nfcService.getAllScans(limit, offset));
    }

    @GetMapping("/validation-rules")
    public ResponseEntity<List<ValidationRule>> getValidationRules() {
        return ResponseEntity.ok(nfcService.getValidationRules());
    }

    @PostMapping("/tags")
    public ResponseEntity<NFCTag> createNFCTag(@Valid @RequestBody CreateNFCTagRequest request) {
        return ResponseEntity.ok(nfcService.createNFCTag(request));
    }

    @GetMapping("/tags/{token}")
    public ResponseEntity<NFCTag> getNFCTagByToken(@PathVariable String token) {
        return ResponseEntity.ok(nfcService.getNFCTagByToken(token));
    }

    @PutMapping("/tags/{token}")
    public ResponseEntity<NFCTag> updateNFCTag(
            @PathVariable String token,
            @Valid @RequestBody UpdateNFCTagRequest request) {
        return ResponseEntity.ok(nfcService.updateNFCTag(token, request));
    }

    @DeleteMapping("/tags/{token}")
    public ResponseEntity<Void> deleteNFCTag(@PathVariable String token) {
        nfcService.deleteNFCTag(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate/{token}")
    public ResponseEntity<Void> validateTag(
            @PathVariable String token,
            @RequestParam Integer userId) {
        nfcService.validateTag(token, userId);
        return ResponseEntity.ok().build();
    }
}