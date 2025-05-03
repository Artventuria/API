package com.artventuria.api.repository.jpa.nfc;

import com.artventuria.api.domain.postgresql.NFCTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NFCTagRepository extends JpaRepository<NFCTag, Long> {
    Optional<NFCTag> findByToken(String token);

    @Query("SELECT t FROM NFCTag t LEFT JOIN FETCH t.artwork WHERE t.token = :token")
    Optional<NFCTag> findByTokenWithArtwork(String token);

    @Query("SELECT t FROM NFCTag t LEFT JOIN FETCH t.artwork WHERE t.token LIKE :tokenPrefix%")
    List<NFCTag> findByTokenStartingWithAndFetchArtwork(String tokenPrefix);

    List<NFCTag> findByArtworkId(Long artworkId);

    List<NFCTag> findByIsActive(Boolean isActive);

    boolean existsByToken(String token);

    Optional<NFCTag> findByArtworkIdAndIsActive(Long artworkId, Boolean isActive);

    @Query("SELECT t FROM NFCTag t LEFT JOIN FETCH t.artwork")
    List<NFCTag> findAllWithArtwork();
}