package com.artventuria.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import com.artventuria.api.mapper.ArtworkMapper;
import com.artventuria.api.service.admin.AdminNFCService;
import com.artventuria.api.dto.artwork.ArtworkDTO;
import com.artventuria.api.dto.nfc.NFCTagDTO;
import com.artventuria.api.dto.nfc.*;
import com.artventuria.api.domain.postgresql.NFCValidationRule;
import com.artventuria.api.domain.postgresql.NFCTag;
import com.artventuria.api.domain.mongodb.ScanLog;
import com.artventuria.api.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/api/admin/nfc")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNFCController {

    private final AdminNFCService adminNFCService;
    private final ArtworkMapper artworkMapper;

    public AdminNFCController(AdminNFCService adminNFCService, ArtworkMapper artworkMapper) {
        this.adminNFCService = adminNFCService;
        this.artworkMapper = artworkMapper;
    }

    @GetMapping("/artwork/{artworkId}/scans")
    public ResponseEntity<List<ScanLog>> getArtworkScanAnalytics(
            @PathVariable Integer artworkId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(adminNFCService.getArtworkScanAnalytics(artworkId, startDate, endDate));
    }

    @GetMapping("/scans/time-range")
    public ResponseEntity<List<ScanLog>> getScansByTimeRange(
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end) {
        String effectiveStartDate = startDate != null ? startDate : start;
        String effectiveEndDate = endDate != null ? endDate : end;
        if (effectiveStartDate == null || effectiveEndDate == null) {
            throw new IllegalArgumentException("Either startDate/endDate or start/end parameters must be provided");
        }
        return ResponseEntity.ok(adminNFCService.getScansByTimeRange(effectiveStartDate, effectiveEndDate));
    }

    @GetMapping("/validation-rules")
    public ResponseEntity<List<NFCValidationRule>> getValidationRules() {
        return ResponseEntity.ok(adminNFCService.getValidationRules());
    }

    @PostMapping("/validation-rules")
    public ResponseEntity<NFCValidationRule> createValidationRule(
            @Valid @RequestBody CreateNFCValidationRuleRequest request) {
        return ResponseEntity.ok(adminNFCService.createValidationRule(request));
    }

    @PutMapping("/validation-rules/{id}")
    public ResponseEntity<NFCValidationRule> updateValidationRule(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateNFCValidationRuleRequest request) {
        return ResponseEntity.ok(adminNFCService.updateValidationRule(id, request));
    }

    @DeleteMapping("/validation-rules/{id}")
    public ResponseEntity<Void> deleteValidationRule(@PathVariable Integer id) {
        adminNFCService.deleteValidationRule(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags")
    public ResponseEntity<?> createNFCTag(@Valid @RequestBody AdminCreateNFCTagRequest request) {
        try {
            NFCTag nfcTag = adminNFCService.createNFCTag(request.getToken(), request.getArtwork_id(),
                    request.isActive());
            
            // Convert artwork to DTO to load metadata
            ArtworkDTO artworkDTO = artworkMapper.toDto(nfcTag.getArtwork());
            
            // Create a DTO for the NFCTag with the ArtworkDTO
            NFCTagDTO nfcTagDTO = new NFCTagDTO();
            nfcTagDTO.setId(nfcTag.getId());
            nfcTagDTO.setToken(nfcTag.getToken());
            nfcTagDTO.setArtwork(artworkDTO);
            nfcTagDTO.setCreatedAt(nfcTag.getCreatedAt());
            nfcTagDTO.setUpdatedAt(nfcTag.getUpdatedAt());
            nfcTagDTO.setActive(nfcTag.isActive());
            
            return ResponseEntity.ok(nfcTagDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/tags")
    public ResponseEntity<List<NFCTag>> getAllNFCTags() {
        return ResponseEntity.ok(adminNFCService.getAllNFCTags());
    }

    @GetMapping("/tags/{token}")
    public ResponseEntity<List<NFCTag>> getNFCTagByToken(@PathVariable String token) {
        List<NFCTag> tags = adminNFCService.getNFCTagByToken(token);
        return ResponseEntity.ok(tags);
    }

    @DeleteMapping("/tags/id/{id}")
    public ResponseEntity<Void> deleteNFCTagById(@PathVariable Integer id) {
        try {
            adminNFCService.deleteNFCTagById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}