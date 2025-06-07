package com.artventuria.api.service.artwork;

import java.util.List;
import com.artventuria.api.domain.postgresql.Artwork;
import com.artventuria.api.dto.artwork.StillToCollectResponse;

public interface ArtworkService {
    Artwork createArtwork(Artwork artwork);

    Artwork getArtworkById(Integer artworkId);

    List<Artwork> getUserArtworks(String artist, int limit, int offset);

    List<Artwork> searchArtworks(String query, int limit, int offset);

    void updateArtwork(Integer artworkId, Artwork artwork);

    void patchArtwork(Integer artworkId, Artwork partialArtwork);

    void deleteArtwork(Integer artworkId);

    /**
     * Returns a random list of artworks not scanned by the user.
     * Suitable for infinite scrolling, with slider-based
     * pagination.
     * 
     * @param userId User ID
     * @param limit  Number of artworks to return per page
     * @param cursor Pagination cursor (null for first page)
     * @return List of artworks to collect with cursor for next page
     */
    List<StillToCollectResponse> getStillToCollectArtworks(Integer userId, int limit, String cursor);
}