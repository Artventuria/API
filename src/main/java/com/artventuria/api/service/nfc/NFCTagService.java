package com.artventuria.api.service.nfc;

import com.artventuria.api.domain.postgresql.NFCTag;
import com.artventuria.api.dto.nfc.CreateNFCTagRequest;
import com.artventuria.api.dto.nfc.UpdateNFCTagRequest;

/**
 * Service dedicated to managing NFC tags
 */
public interface NFCTagService {

    /**
     * Get an NFC tag by ID
     * 
     * @param id ID of the NFC tag
     * @return The corresponding NFC tag
     */
    NFCTag getTag(Integer id);

    /**
     * Update an NFC tag
     * 
     * @param id         ID of the NFC tag
     * @param updatedTag NFC tag with the modifications
     * @return The updated NFC tag
     */
    NFCTag updateTag(Integer id, NFCTag updatedTag);

    /**
     * Delete an NFC tag
     * 
     * @param id ID of the NFC tag
     */
    void deleteTag(Integer id);

    /**
     * Create a new NFC tag from a request
     * 
     * @param request Request to create
     * @return The created NFC tag
     */
    NFCTag createNFCTag(CreateNFCTagRequest request);

    /**
     * Create a new NFC tag
     * 
     * @param tag NFC tag to create
     * @return The created NFC tag
     */
    NFCTag createTag(NFCTag tag);

    /**
     * Get an NFC tag by token
     * 
     * @param token Token of the NFC tag
     * @return The corresponding NFC tag
     */
    NFCTag getNFCTagByToken(String token);

    /**
     * Update an NFC tag by token
     * 
     * @param token   Token of the NFC tag
     * @param request Request to update
     * @return The updated NFC tag
     */
    NFCTag updateNFCTag(String token, UpdateNFCTagRequest request);

    /**
     * Delete an NFC tag by token
     * 
     * @param token Token of the NFC tag
     */
    void deleteNFCTag(String token);
}
