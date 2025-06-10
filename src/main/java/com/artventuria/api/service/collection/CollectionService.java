package com.artventuria.api.service.collection;

import java.util.List;
import com.artventuria.api.domain.postgresql.Collection;
import com.artventuria.api.domain.postgresql.Artwork;
import com.artventuria.api.dto.artwork.ArtworkDTO;

public interface CollectionService {

    Collection getCollectionById(Integer collectionId);

    List<Collection> getUserCollections(Integer userId, int limit, int offset);

    void addArtworkToCollection(Integer collectionId, Integer artworkId);

    List<Artwork> getCollectionArtworks(Integer collectionId, int limit, int offset);

    boolean isArtworkInCollection(Integer collectionId, Integer artworkId);

    int getCollectionArtworksCount(Integer collectionId);
    
    /**
     * Get recently collected artworks across all collections of a user within a specific time period
     * 
     * @param userId ID of the user
     * @param hoursAgo Number of hours to look back (e.g., 168 for one week)
     * @return List of complete artwork DTOs recently collected by the user within the specified time period
     */
    List<ArtworkDTO> getRecentlyCollectedArtworks(Integer userId, int hoursAgo);
    
    /**
     * Check if an artwork is in any of the user's collections
     * 
     * @param userId ID of the user
     * @param artworkId ID of the artwork to check
     * @return true if the artwork is in any of the user's collections, false otherwise
     */
    boolean isArtworkInUserCollection(Integer userId, Integer artworkId);
    
    /**
     * Count the number of unique users who have collected a specific artwork
     * 
     * @param artworkId ID of the artwork
     * @return Number of unique users who have collected the artwork
     */
    int countUsersByArtworkId(Integer artworkId);
    
    /**
     * Get all artworks collected by a user across all their collections
     * 
     * @param userId ID of the user
     * @param limit Maximum number of items to return
     * @param offset Number of items to skip for pagination
     * @return List of ArtworkDTO representing all the artworks collected by the user
     */
    List<ArtworkDTO> getAllUserCollectedArtworks(Integer userId, int limit, int offset);
    
    /**
     * Count the total number of artworks collected by a user across all their collections
     * 
     * @param userId ID of the user
     * @return Total count of artworks collected by the user
     */
    int countUserCollectedArtworks(Integer userId);
    
    /**
     * Search for artworks collected by a user across all their collections
     * 
     * @param userId ID of the user
     * @param query Search query string
     * @param limit Maximum number of items to return
     * @param offset Number of items to skip for pagination
     * @return List of ArtworkDTO objects matching the search criteria
     */
    List<ArtworkDTO> searchUserCollectedArtworks(Integer userId, String query, int limit, int offset);
}