package com.artventuria.api.service.nfc.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.artventuria.api.service.nfc.NFCScanService;
import com.artventuria.api.service.nfc.NFCService;
import com.artventuria.api.service.nfc.NFCTagService;
import com.artventuria.api.domain.mongodb.ScanLog;
import com.artventuria.api.domain.postgresql.ValidationRule;
import com.artventuria.api.domain.postgresql.NFCTag;
import com.artventuria.api.dto.nfc.*;
import com.artventuria.api.repository.jpa.validation.ValidationRuleRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NFCServiceImpl implements NFCService {
    private final ValidationRuleRepository validationRuleRepository;
    private final NFCTagService nfcTagService;
    private final NFCScanService nfcScanService;

    @Override
    @Transactional(readOnly = true)
    public List<ScanLog> getUserScans(Integer userId, int limit, int offset) {
        return nfcScanService.getUserScans(userId, limit, offset);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScanLog> getArtworkScans(Integer artworkId) {
        return nfcScanService.getArtworkScans(artworkId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScanLog> getScansByTimeRange(Instant startTime, Instant endTime) {
        return nfcScanService.getScansByTimeRange(startTime, endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScanLog> getAllScans(int limit, int offset) {
        return nfcScanService.getAllScans(limit, offset);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidationRule> getValidationRules() {
        return validationRuleRepository.findAll();
    }

    @Override
    @Transactional
    public NFCScanResponse processNFCScan(NFCScanRequest request) {
        return nfcScanService.processNFCScan(request);
    }

    @Override
    @Transactional
    public NFCTag getTag(Integer id) {
        return nfcTagService.getTag(id);
    }

    @Override
    @Transactional
    public NFCTag updateTag(Integer id, NFCTag updatedTag) {
        return nfcTagService.updateTag(id, updatedTag);
    }

    @Override
    @Transactional
    public void deleteTag(Integer id) {
        nfcTagService.deleteTag(id);
    }

    @Override
    @Transactional
    public NFCTag createNFCTag(CreateNFCTagRequest request) {
        return nfcTagService.createNFCTag(request);
    }

    @Override
    @Transactional
    public NFCTag createTag(NFCTag tag) {
        return nfcTagService.createTag(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public NFCTag getNFCTagByToken(String token) {
        return nfcTagService.getNFCTagByToken(token);
    }

    @Override
    @Transactional
    public NFCTag updateNFCTag(String token, UpdateNFCTagRequest request) {
        return nfcTagService.updateNFCTag(token, request);
    }

    @Override
    @Transactional
    public void deleteNFCTag(String token) {
        nfcTagService.deleteNFCTag(token);
    }

    @Override
    @Transactional
    public void validateAndScanTag(String token, Integer userId, String location, String deviceId, String ipAddress) {
        nfcScanService.validateAndScanTag(token, userId, location, deviceId, ipAddress);
    }

    @Override
    @Transactional
    public void validateTag(String token, Integer userId) {
        nfcScanService.validateTag(token, userId);
    }
}