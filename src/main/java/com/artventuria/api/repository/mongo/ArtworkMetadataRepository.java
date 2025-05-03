package com.artventuria.api.repository.mongo;

import com.artventuria.api.domain.mongodb.ArtworkMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("artworkMetadataMongoRepository")
public interface ArtworkMetadataRepository extends MongoRepository<ArtworkMetadata, String> {
    Optional<ArtworkMetadata> findByArtworkId(Long artworkId);
    
    void deleteByArtworkId(Long artworkId);
    
    boolean existsByArtworkId(Long artworkId);
}