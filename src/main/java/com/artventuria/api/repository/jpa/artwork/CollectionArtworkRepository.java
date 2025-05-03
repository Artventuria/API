package com.artventuria.api.repository.jpa.artwork;

import com.artventuria.api.domain.postgresql.CollectionArtwork;
import com.artventuria.api.domain.postgresql.CollectionArtworkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionArtworkRepository extends JpaRepository<CollectionArtwork, CollectionArtworkId> {
    List<CollectionArtwork> findByCollectionId(Integer collectionId);

    boolean existsByCollectionIdAndArtworkId(Integer collectionId, Integer artworkId);

    @Query("SELECT ca FROM CollectionArtwork ca JOIN ca.collection c WHERE c.userId = :userId AND ca.artworkId = :artworkId")
    List<CollectionArtwork> findByUserIdAndArtworkId(Integer userId, Integer artworkId);
}
