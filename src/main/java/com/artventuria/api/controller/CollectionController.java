package com.artventuria.api.controller;

import com.artventuria.api.domain.postgresql.Collection;
import com.artventuria.api.domain.postgresql.Artwork;
import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.repository.jpa.user.UserRepository;
import com.artventuria.api.service.collection.CollectionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private final CollectionService collectionService;
    private final UserRepository userRepository;

    @Autowired
    public CollectionController(CollectionService collectionService, UserRepository userRepository) {
        this.collectionService = collectionService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{collectionId}")
    public ResponseEntity<Collection> getCollection(@PathVariable Integer collectionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Collection collection = collectionService.getCollectionById(collectionId);

        // Check if the collection belongs to the authenticated user
        if (!collection.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Detach the User object to avoid LazyInitializationException
        collection.setUser(null);

        return ResponseEntity.ok(collection);
    }

    // Route getUserCollections removed

    @GetMapping("/me")
    public ResponseEntity<List<Collection>> getMyCollections(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        List<Collection> collections = collectionService.getUserCollections(user.getId(), limit, offset);

        for (Collection collection : collections) {
            collection.setUser(null);
        }

        return ResponseEntity.ok(collections);
    }

    @GetMapping("/{collectionId}/artworks")
    public ResponseEntity<List<Artwork>> getCollectionArtworks(
            @PathVariable Integer collectionId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Collection collection = collectionService.getCollectionById(collectionId);

        // Check if the collection belongs to the authenticated user
        if (!collection.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Artwork> artworks = collectionService.getCollectionArtworks(collectionId, limit, offset);

        // Detach the lazy references in the artworks
        // Note: If Artwork has lazy references, it would need to be handled here

        return ResponseEntity.ok(artworks);
    }

    @GetMapping("/{collectionId}/artworks/count")
    public ResponseEntity<Integer> getCollectionArtworksCount(@PathVariable Integer collectionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Collection collection = collectionService.getCollectionById(collectionId);

        // Check if the collection belongs to the authenticated user
        if (!collection.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(collectionService.getCollectionArtworksCount(collectionId));
    }
}