package com.artventuria.api.service.nfc.impl;

import com.artventuria.api.domain.mongodb.ArtworkMetadata;
import com.artventuria.api.domain.mongodb.ScanLog;
import com.artventuria.api.domain.postgresql.Badge;
import com.artventuria.api.domain.postgresql.Collection;
import com.artventuria.api.domain.postgresql.NFCTag;
import com.artventuria.api.domain.postgresql.User;

import com.artventuria.api.dto.nfc.NFCScanRequest;
import com.artventuria.api.dto.nfc.NFCScanResponse;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.repository.jpa.artwork.CollectionRepository;
import com.artventuria.api.repository.jpa.badge.BadgeRepository;
import com.artventuria.api.repository.jpa.user.UserRepository;
import com.artventuria.api.repository.mongo.ArtworkMetadataRepository;
import com.artventuria.api.repository.mongo.ScanLogRepository;
import com.artventuria.api.service.badge.BadgeProgressService;
import com.artventuria.api.service.collection.CollectionService;
import com.artventuria.api.service.nfc.NFCScanService;
import com.artventuria.api.service.nfc.NFCTagService;
import com.artventuria.api.service.point.PointService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NFCScanServiceImpl implements NFCScanService {

    private final ScanLogRepository scanLogRepository;
    private final CollectionService collectionService;
    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;
    private final BadgeProgressService badgeProgressService;
    private final BadgeRepository badgeRepository;
    private final NFCTagService nfcTagService;
    private final PointService pointService;

    @Autowired
    private ArtworkMetadataRepository artworkMetadataRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ScanLog> getUserScans(Integer userId, int limit, int offset) {
        return scanLogRepository.findByUserIdOrderByTimestampDesc(userId.longValue())
                .stream()
                .skip(offset)
                .limit(limit)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScanLog> getArtworkScans(Integer artworkId) {
        return scanLogRepository.findByArtworkIdOrderByTimestampDesc(artworkId.longValue());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScanLog> getScansByTimeRange(Instant startTime, Instant endTime) {
        return scanLogRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(
                null,
                startTime,
                endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScanLog> getAllScans(int limit, int offset) {
        return scanLogRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .skip(offset)
                .limit(limit)
                .toList();
    }

    @Override
    @Transactional
    public NFCScanResponse processNFCScan(NFCScanRequest request) {
        // 1. Get the NFC tag
        NFCTag tag = nfcTagService.getNFCTagByToken(request.getToken());

        // Create the base response (which will be returned even if userId is not
        // provided)
        NFCScanResponse response = new NFCScanResponse();
        response.setLocation(request.getLocation());
        response.setDeviceId(request.getDeviceId());
        response.setToken(tag.getToken());

        // If the artworkId is available on the tag, add it to the response
        if (tag.getArtwork() != null) {
            response.setArtworkId(tag.getArtwork().getId().longValue());
        }

        // Define tagId
        response.setTagId(tag.getId().longValue());

        // If no user ID is provided, it's just a validation scan without connection
        // Return the base info
        if (request.getUserId() == null) {
            response.setStatus("Success");
            return response;
        }

        // Define the user ID
        response.setUserId(request.getUserId().intValue());

        // 2. Check if the user exists
        User user = userRepository.findById(request.getUserId().intValue())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Add the artwork to the user's collection if it's not already added
        boolean artworkAdded = false;
        if (tag.getArtwork() != null) {
            // Get the user's default collection
            List<Collection> userCollections = collectionRepository.findByUserId(request.getUserId().intValue());
            if (userCollections.isEmpty()) {
                System.out.println("No collection found for user ID: " + request.getUserId());
                response.setStatus("Success");
                return response;
            }

            Collection userCollection = userCollections.get(0); // The default collection of the user
            System.out
                    .println("Found collection ID: " + userCollection.getId() + " for user ID: " + request.getUserId());

            // 4. Check if the artwork is already in the collection
            boolean artworkAlreadyInCollection = collectionService.isArtworkInCollection(userCollection.getId(),
                    tag.getArtwork().getId());

            // Always initialize to false
            artworkAdded = false;

            if (!artworkAlreadyInCollection) {
                try {
                    // Add the artwork to the collection - The method is void, no return
                    collectionService.addArtworkToCollection(userCollection.getId(), tag.getArtwork().getId());
                    // If no exception is raised, consider it successful
                    artworkAdded = true;

                    // If the artworkId has rarity points, assign them to the user
                    if (tag.getArtwork().getRarityPoints() != null && tag.getArtwork().getRarityPoints() > 0) {
                        Integer points = tag.getArtwork().getRarityPoints();
                        response.setPointsEarned(points);

                        // Add the rarity points to the user's account
                        try {
                            // Use addPoints which is in the PointService interface
                            pointService.addPoints(
                                    request.getUserId().intValue(),
                                    points);
                        } catch (Exception e) {
                            System.err.println("Error adding points to user: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    response.setStatus("Success");
                } catch (Exception e) {
                    System.err.println("Error adding artwork to collection: " + e.getMessage());
                    e.printStackTrace();
                    response.setStatus("Error");
                }
            } else {
                // The artwork is already in the user's collection
                response.setStatus("AlreadyCollected");
            }
        } else {
            // No artwork associated with the NFC tag
            response.setStatus("Success");
        }

        // 5. Save the scan in the database
        ScanLog scanLog = new ScanLog();
        scanLog.setUserId(request.getUserId().longValue());
        if (tag.getArtwork() != null) {
            scanLog.setArtworkId(tag.getArtwork().getId().longValue());
        }
        scanLog.setLocation(request.getLocation() != null ? request.getLocation() : "unknown");
        scanLog.setDeviceId(request.getDeviceId());
        // Define the tagId which is required by the MongoDB schema
        scanLog.setTagId(tag.getId().longValue());
        // Define the scan as valid
        scanLog.setValid(true);
        scanLog.setTimestamp(Instant.now());

        scanLogRepository.save(scanLog);

        // 6. Update the badge progress
        updateBadgeProgressForNFCScan(user, tag, request.getLocation(), artworkAdded);

        return response;
    }

    @Override
    public void updateBadgeProgressForNFCScan(User user, NFCTag tag, String location, boolean artworkAdded) {
        try {
            List<Badge> allBadges = badgeRepository.findAll();

            // Process all badges according to their types
            for (Badge badge : allBadges) {
                String badgeType = getBadgeType(badge);

                // Collection badges - update only if it's a new artwork
                if ("collection".equals(badgeType) && artworkAdded) {
                    badgeProgressService.updateProgress(user.getId(), badge.getId(), 1);
                    System.out.println(
                            "Collection badge progress updated: " + badge.getName() + ", user: " + user.getId());
                }

                else if ("location".equals(badgeType)) {
                    badgeProgressService.updateProgress(user.getId(), badge.getId(), 0); // 0 because the counting is
                                                                                         // done elsewhere
                    System.out.println(
                            "Location badge progress check triggered: " + badge.getName() + ", user: " + user.getId());
                }

                // Streak badges - record daily activity
                else if ("streak".equals(badgeType)) {
                    // For streaks, we simply note the activity of the user today
                    // This information would be used by a separate process to update
                    // the streaks
                    System.out.println(
                            "Streak activity recorded for user " + user.getId() + " at " + Instant.now());

                    // Same if it's not a new artwork, update the streak badge
                    // because the user has been active today
                    badgeProgressService.updateProgress(user.getId(), badge.getId(), 1); // Increment by 1 for each day
                                                                                         // active
                }

                // Materials diversity badges - based on the diversity of the materials of
                // the artworks
                else if ("materials_diversity".equals(badgeType) && artworkAdded && tag.getArtwork() != null) {
                    // We only process this badge if it's a new scan of an artwork
                    try {
                        Long artworkId = tag.getArtwork().getId().longValue();

                        // Use the injected repository to retrieve the artwork metadata
                        Optional<ArtworkMetadata> metadata = artworkMetadataRepository.findByArtworkId(artworkId);

                        if (metadata.isPresent() && metadata.get().getMaterials() != null
                                && !metadata.get().getMaterials().isEmpty()) {
                            // Check if the user has scanned artworks with these materials
                            List<String> artworkMaterials = metadata.get().getMaterials();

                            // Get the user's previous scans
                            List<ScanLog> userScans = scanLogRepository
                                    .findByUserIdOrderByTimestampDesc(user.getId().longValue());

                            // Collect all the materials seen by the user
                            List<String> previouslyEncounteredMaterials = new ArrayList<>();

                            for (ScanLog scan : userScans) {
                                // Ignore the current scan (which has just been recorded)
                                if (scan.getTimestamp()
                                        .isAfter(Instant.now().minus(1, ChronoUnit.MINUTES))) {
                                    continue;
                                }

                                // Get the metadata of the previously scanned artwork
                                Optional<ArtworkMetadata> scanMetadata = artworkMetadataRepository
                                        .findByArtworkId(scan.getArtworkId());

                                if (scanMetadata.isPresent() && scanMetadata.get().getMaterials() != null) {
                                    previouslyEncounteredMaterials.addAll(scanMetadata.get().getMaterials());
                                }
                            }

                            // Find the new materials not previously encountered
                            boolean newMaterialFound = false;
                            for (String material : artworkMaterials) {
                                if (!previouslyEncounteredMaterials.contains(material)) {
                                    // New material found, increment the badge
                                    badgeProgressService.updateProgress(user.getId(), badge.getId(), 1);
                                    System.out.println("Materials diversity badge progress updated for material: " +
                                            material + ", badge: " + badge.getName() + ", user: " + user.getId());
                                    newMaterialFound = true;
                                    break; // We only count one new material per scan
                                }
                            }

                            if (!newMaterialFound) {
                                System.out.println("No new materials found for user " + user.getId());
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing materials diversity badge: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating badge progress: " + e.getMessage());
        }
    }

    /**
     * Extract the badge type from its criteria
     */
    private String getBadgeType(Badge badge) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> criteria = mapper.readValue(badge.getCriteria(), Map.class);
            return criteria != null ? (String) criteria.get("type") : null;
        } catch (Exception e) {
            System.err.println("Error parsing badge criteria: " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public void validateAndScanTag(String token, Integer userId, String location, String deviceId, String ipAddress) {
        NFCTag tag = nfcTagService.getNFCTagByToken(token);

        if (!tag.isActive()) {
            throw new IllegalStateException("NFC tag is not active");
        }

        ScanLog scanLog = new ScanLog();
        scanLog.setUserId(userId.longValue());
        scanLog.setArtworkId(tag.getArtwork().getId().longValue());
        scanLog.setLocation(location);
        scanLog.setDeviceId(deviceId);
        scanLog.setIpAddress(ipAddress);
        scanLog.setTimestamp(Instant.now());

        scanLogRepository.save(scanLog);
    }

    @Override
    @Transactional
    public void validateTag(String token, Integer userId) {
        NFCTag tag = nfcTagService.getNFCTagByToken(token);

        if (!tag.isActive()) {
            throw new IllegalStateException("NFC tag is not active");
        }
    }
}
