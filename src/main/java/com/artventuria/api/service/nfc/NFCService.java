package com.artventuria.api.service.nfc;

import java.time.Instant;
import java.util.List;

import com.artventuria.api.domain.mongodb.ScanLog;
import com.artventuria.api.domain.postgresql.ValidationRule;
import com.artventuria.api.domain.postgresql.NFCTag;
import com.artventuria.api.dto.nfc.NFCScanRequest;
import com.artventuria.api.dto.nfc.NFCScanResponse;
import com.artventuria.api.dto.nfc.CreateNFCTagRequest;
import com.artventuria.api.dto.nfc.UpdateNFCTagRequest;

public interface NFCService {
    void validateAndScanTag(String token, Integer userId, String location, String deviceId, String ipAddress);

    List<ScanLog> getUserScans(Integer userId, int limit, int offset);

    List<ScanLog> getArtworkScans(Integer artworkId);

    List<ScanLog> getScansByTimeRange(Instant startTime, Instant endTime);

    List<ScanLog> getAllScans(int limit, int offset);

    List<ValidationRule> getValidationRules();

    NFCScanResponse processNFCScan(NFCScanRequest request);

    NFCTag createNFCTag(CreateNFCTagRequest request);

    NFCTag createTag(NFCTag tag);

    NFCTag getNFCTagByToken(String token);

    NFCTag getTag(Integer id);

    NFCTag updateNFCTag(String token, UpdateNFCTagRequest request);

    NFCTag updateTag(Integer id, NFCTag tag);

    void deleteNFCTag(String token);

    void deleteTag(Integer id);

    void validateTag(String token, Integer userId);
}