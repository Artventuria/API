package com.artventuria.api.service.artwork;

import java.util.List;
import com.artventuria.api.domain.postgresql.Artwork;

public interface ArtworkService {
    Artwork createArtwork(Artwork artwork);

    Artwork getArtworkById(Integer artworkId);

    List<Artwork> getUserArtworks(String artist, int limit, int offset);

    List<Artwork> searchArtworks(String query, int limit, int offset);

    void updateArtwork(Integer artworkId, Artwork artwork);
    
    void patchArtwork(Integer artworkId, Artwork partialArtwork);

    void deleteArtwork(Integer artworkId);

}