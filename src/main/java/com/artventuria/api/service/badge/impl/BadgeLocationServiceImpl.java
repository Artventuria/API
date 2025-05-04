package com.artventuria.api.service.badge.impl;

import com.artventuria.api.domain.mongodb.ScanLog;
import com.artventuria.api.repository.jpa.artwork.ArtworkRepository;
import com.artventuria.api.repository.mongo.ScanLogRepository;
import com.artventuria.api.service.badge.BadgeLocationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the BadgeLocationService
 */
@Service
public class BadgeLocationServiceImpl implements BadgeLocationService {

    @Autowired
    private ScanLogRepository scanLogRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    @Override
    public int countUniqueVenuesForUser(Long userId) {
        // Retrieve all valid scans for the user
        List<ScanLog> validScans = scanLogRepository.findValidArtworkScansByUserId(userId);

        // Extract unique artwork IDs from valid scans
        Set<Long> uniqueArtworkIds = validScans.stream()
                .map(ScanLog::getArtworkId)
                .collect(Collectors.toSet());

        // If no valid scans, return 0
        if (uniqueArtworkIds.isEmpty()) {
            return 0;
        }

        // Convert IDs to list for SQL query
        List<Long> artworkIdsList = uniqueArtworkIds.stream().collect(Collectors.toList());

        // Count the distinct venue_ids for these artworks
        return artworkRepository.countDistinctVenueIdsByArtworkIds(artworkIdsList);
    }
}
