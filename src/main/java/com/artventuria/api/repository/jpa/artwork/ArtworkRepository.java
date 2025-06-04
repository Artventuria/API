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

    /**
     * Find random artworks that a user has not scanned yet, with pagination using a
     * cursor.
     * For infinite scroll "Still to Collect" feature.
     * 
     * @param scannedArtworkIds List of artwork IDs the user has already scanned
     * @param lastId            The last artwork ID from the previous page (cursor),
     *                          null for first page
     * @param limit             Maximum number of artworks to return
     * @return List of random artworks not yet scanned by the user
     */
    @Query(value = "WITH available_artworks AS (SELECT * FROM artworks WHERE (:scannedArtworkIdsEmpty = true OR CAST(id AS bigint) NOT IN (:scannedArtworkIds))), random_artworks AS (SELECT * FROM available_artworks WHERE (:lastId IS NULL OR id > :lastId OR :lastId >= (SELECT MAX(id) FROM available_artworks)) ORDER BY CASE WHEN :lastId IS NULL OR id > :lastId THEN id ELSE 0 END LIMIT :limit) SELECT * FROM random_artworks ORDER BY RANDOM()", nativeQuery = true)
    List<Artwork> findRandomArtworksNotScannedByUser(
            @Param("scannedArtworkIds") List<Long> scannedArtworkIds,
            @Param("scannedArtworkIdsEmpty") boolean scannedArtworkIdsEmpty,
            @Param("lastId") Integer lastId,
            @Param("limit") int limit);

    /**
     * Count the total number of artworks
     * 
     * @return Total number of artworks
     */
    long count();
}