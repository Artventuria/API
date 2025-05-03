package com.artventuria.api.repository.jpa.artwork;

import com.artventuria.api.domain.postgresql.Artwork;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Integer> {
    List<Artwork> findByArtist(String artist);

    @Query(value = "SELECT * FROM artworks a WHERE to_tsvector('english', a.title || ' ' || a.description || ' ' || a.artist) @@ plainto_tsquery('english', :query) ORDER BY ts_rank(to_tsvector('english', a.title || ' ' || a.description || ' ' || a.artist), plainto_tsquery('english', :query)) DESC", nativeQuery = true)
    List<Artwork> searchByFullText(@Param("query") String query, Pageable pageable);

    /**
     * Count the number of distinct venue IDs from a list of artwork IDs
     * 
     * @param artworkIds List of artwork IDs
     * @return Number of distinct non-null venue IDs
     */
    @Query(value = "SELECT COUNT(DISTINCT venue_id) FROM artworks WHERE id IN (:artworkIds) AND venue_id IS NOT NULL", nativeQuery = true)
    int countDistinctVenueIdsByArtworkIds(@Param("artworkIds") List<Long> artworkIds);
}