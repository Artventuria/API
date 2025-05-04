package com.artventuria.api.service.collection;

import java.util.List;
import com.artventuria.api.domain.postgresql.Collection;
import com.artventuria.api.domain.postgresql.Artwork;

public interface CollectionService {

    Collection getCollectionById(Integer collectionId);

    List<Collection> getUserCollections(Integer userId, int limit, int offset);

    void addArtworkToCollection(Integer collectionId, Integer artworkId);

    List<Artwork> getCollectionArtworks(Integer collectionId, int limit, int offset);

    boolean isArtworkInCollection(Integer collectionId, Integer artworkId);

    int getCollectionArtworksCount(Integer collectionId);
}