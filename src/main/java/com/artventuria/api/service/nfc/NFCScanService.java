package com.artventuria.api.service.nfc;

import com.artventuria.api.domain.mongodb.ScanLog;
import com.artventuria.api.domain.postgresql.NFCTag;
import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.dto.nfc.NFCScanRequest;
import com.artventuria.api.dto.nfc.NFCScanResponse;

import java.time.Instant;
import java.util.List;

/**
 * Service dedicated to the management of NFC scans
 */
public interface NFCScanService {

    /**
     * Process an NFC scan
     * 
     * @param request NFC scan request
     * @return NFC scan response
     */
    NFCScanResponse processNFCScan(NFCScanRequest request);

    /**
     * Get the scans for a user
     * 
     * @param userId User ID
     * @param limit  Limit of results
     * @param offset Offset for pagination
     * @return List of scans
     */
    List<ScanLog> getUserScans(Integer userId, int limit, int offset);

    /**
     * Get the scans for an artwork
     * 
     * @param artworkId Artwork ID
     * @return List of scans
     */
    List<ScanLog> getArtworkScans(Integer artworkId);

    /**
     * Get the scans in a time range
     * 
     * @param startTime Start time
     * @param endTime   End time
     * @return List of scans
     */
    List<ScanLog> getScansByTimeRange(Instant startTime, Instant endTime);

    /**
     * Get all scans
     * 
     * @param limit  Limit of results
     * @param offset Offset for pagination
     * @return List of scans
     */
    List<ScanLog> getAllScans(int limit, int offset);

    /**
     * Update badge progress after an NFC scan
     * 
     * @param user         User who scanned the NFC tag
     * @param tag          NFC tag scanned
     * @param location     Location of the scan
     * @param artworkAdded If the artwork was added to the collection (first scan)
     */
    void updateBadgeProgressForNFCScan(User user, NFCTag tag, String location, boolean artworkAdded);

    /**
     * Validate and record an NFC scan
     * 
     * @param token     Token of the NFC tag
     * @param userId    ID of the user
     * @param location  Location of the scan
     * @param deviceId  ID of the device
     * @param ipAddress IP address
     */
    void validateAndScanTag(String token, Integer userId, String location, String deviceId, String ipAddress);

    /**
     * Validate an NFC tag
     * 
     * @param token  Token of the NFC tag
     * @param userId ID of the user
     */
    void validateTag(String token, Integer userId);
}
