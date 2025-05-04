package com.artventuria.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.artventuria.api.service.point.PointService;
import com.artventuria.api.service.user.impl.UserServiceImpl;
import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.domain.mongodb.ScanLog;
import com.artventuria.api.repository.mongo.ScanLogRepository;
import com.artventuria.api.dto.scan.ScanHistoryResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/points")
public class PointController {

    private final PointService pointService;
    private final UserServiceImpl userService;
    private final ScanLogRepository scanLogRepository;

    public PointController(PointService pointService, UserServiceImpl userService,
            ScanLogRepository scanLogRepository) {
        this.pointService = pointService;
        this.userService = userService;
        this.scanLogRepository = scanLogRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<Integer> getCurrentUserPoints() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getCurrentUser(email);
        return ResponseEntity.ok(pointService.getUserPoints(user.getId()));
    }

    @PostMapping("/user/{userId}/add")
    public ResponseEntity<Void> addPoints(
            @PathVariable Integer userId,
            @RequestParam Integer points) {
        pointService.addPoints(userId, points);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/{userId}/subtract")
    public ResponseEntity<Void> subtractPoints(
            @PathVariable Integer userId,
            @RequestParam Integer points) {
        pointService.subtractPoints(userId, points);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/me")
    public ResponseEntity<List<ScanHistoryResponse>> getPointHistory(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getCurrentUser(email);
        List<ScanLog> scanLogs = scanLogRepository.findByUserIdOrderByTimestampDesc(user.getId().longValue());

        List<ScanHistoryResponse> response = scanLogs.stream()
                .skip(offset)
                .limit(limit)
                .map(this::convertToScanHistoryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private ScanHistoryResponse convertToScanHistoryResponse(ScanLog scanLog) {
        ScanHistoryResponse response = new ScanHistoryResponse();
        response.setId(scanLog.getId());
        response.setUserId(scanLog.getUserId());
        response.setArtworkId(scanLog.getArtworkId());
        response.setPoints(1000); // TODO: Default value for the example, change it
        response.setType("artwork_discovery");
        response.setCreatedAt(scanLog.getTimestamp());
        return response;
    }

    @PostMapping("/award")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> awardPoints(
            @RequestParam Integer userId,
            @RequestParam Integer artworkId,
            @RequestParam String reason) {
        pointService.awardPoints(userId, artworkId, reason);
        return ResponseEntity.ok().build();
    }
}