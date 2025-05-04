package com.artventuria.api.service.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.artventuria.api.repository.jpa.artwork.ArtworkRepository;
import com.artventuria.api.repository.jpa.nfc.NFCTagRepository;
import com.artventuria.api.repository.jpa.nfc.NFCValidationRuleRepository;
import com.artventuria.api.repository.mongo.ScanLogRepository;
import com.artventuria.api.service.artwork.ArtworkService;
import com.artventuria.api.domain.postgresql.NFCTag;
import com.artventuria.api.domain.postgresql.Artwork;
import com.artventuria.api.domain.postgresql.NFCValidationRule;
import com.artventuria.api.domain.mongodb.ScanLog;
import com.artventuria.api.dto.nfc.*;
import com.artventuria.api.exception.ResourceNotFoundException;
import java.util.List;
import java.time.Instant;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class AdminNFCService {
    private final NFCTagRepository nfcTagRepository;
    private final ArtworkRepository artworkRepository;
    private final NFCValidationRuleRepository validationRuleRepository;
    private final ScanLogRepository scanLogRepository;
    private final ArtworkService artworkService;

    @Transactional
    public NFCTag createNFCTag(String token, Integer artworkId, boolean isActive) {
        // Check if a tag with the same token already exists
        if (nfcTagRepository.existsByToken(token)) {
            throw new IllegalArgumentException("NFC tag with token '" + token + "' already exists");
        }

        Artwork artwork = artworkService.getArtworkById(artworkId);

        // Check if the artwork already has an NFC tag
        List<NFCTag> existingTags = nfcTagRepository.findByArtworkId(artworkId.longValue());
        if (!existingTags.isEmpty()) {
            throw new IllegalArgumentException("Artwork with ID '" + artworkId + "' already has an NFC tag");
        }

        NFCTag nfcTag = new NFCTag();
        nfcTag.setToken(token);
        nfcTag.setArtwork(artwork);
        nfcTag.setActive(isActive);

        // Save the NFC tag first to get its ID
        NFCTag savedNfcTag = nfcTagRepository.save(nfcTag);

        // Now update the artwork to reference this NFC tag
        artwork.setNfcTagId(savedNfcTag.getId().intValue());
        artworkRepository.save(artwork);

        return savedNfcTag;
    }

    @Transactional
    public void deleteNFCTagById(Integer id) {
        NFCTag nfcTag = nfcTagRepository.findById(id.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("NFC tag not found"));
        nfcTagRepository.delete(nfcTag);
    }

    @Transactional(readOnly = true)
    public List<NFCTag> getAllNFCTags() {
        // Use a join fetch query to eagerly load the artwork relationship
        List<NFCTag> tags = nfcTagRepository.findAllWithArtwork();
        return tags;
    }

    @Transactional(readOnly = true)
    public List<NFCTag> getNFCTagByToken(String token) {
        return nfcTagRepository.findByTokenStartingWithAndFetchArtwork(token);
    }

    @Transactional(readOnly = true)
    public List<ScanLog> getArtworkScanAnalytics(Integer artworkId, String startDate, String endDate) {
        // Get the scan logs for the artwork from MongoDB
        List<ScanLog> scanLogs;

        // If date range is provided, filter by date range
        if (startDate != null && endDate != null) {
            Instant start = Instant.parse(startDate).atOffset(ZoneOffset.UTC).toInstant();
            Instant end = Instant.parse(endDate).atOffset(ZoneOffset.UTC).toInstant();
            scanLogs = scanLogRepository.findByArtworkIdAndTimestampBetweenOrderByTimestampDesc(
                    artworkId.longValue(), start, end);
        } else {
            // Otherwise, get all scans for the artwork
            scanLogs = scanLogRepository.findByArtworkIdOrderByTimestampDesc(artworkId.longValue());
        }

        // Return the raw scan logs directly
        return scanLogs;
    }

    @Transactional(readOnly = true)
    public List<ScanLog> getScansByTimeRange(String startDate, String endDate) {
        // Convert UTC ISO string to Instant while preserving UTC timezone
        Instant start = Instant.parse(startDate).atOffset(ZoneOffset.UTC).toInstant();
        Instant end = Instant.parse(endDate).atOffset(ZoneOffset.UTC).toInstant();

        // Get all scans within the time range and return them directly without
        // aggregation
        return scanLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }

    @Transactional(readOnly = true)
    public List<NFCValidationRule> getValidationRules() {
        return validationRuleRepository.findAll();
    }

    @Transactional
    public NFCValidationRule createValidationRule(CreateNFCValidationRuleRequest request) {
        NFCValidationRule rule = new NFCValidationRule();
        // Set properties from request
        return validationRuleRepository.save(rule);
    }

    @Transactional
    public NFCValidationRule updateValidationRule(Integer id, UpdateNFCValidationRuleRequest request) {
        NFCValidationRule rule = validationRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Validation rule not found"));
        // Update properties from request
        return validationRuleRepository.save(rule);
    }

    @Transactional
    public void deleteValidationRule(Integer id) {
        if (!validationRuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Validation rule not found");
        }
        validationRuleRepository.deleteById(id);
    }
}