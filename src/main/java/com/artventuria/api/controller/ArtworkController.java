package com.artventuria.api.controller;

import com.artventuria.api.domain.postgresql.Artwork;
import com.artventuria.api.dto.artwork.ArtworkDTO;
import com.artventuria.api.mapper.ArtworkMapper;
import com.artventuria.api.service.artwork.ArtworkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    private final ArtworkService artworkService;
    private final ArtworkMapper artworkMapper;

    @Autowired
    public ArtworkController(ArtworkService artworkService, ArtworkMapper artworkMapper) {
        this.artworkService = artworkService;
        this.artworkMapper = artworkMapper;
    }

    @PostMapping
    public ResponseEntity<ArtworkDTO> createArtwork(@RequestBody ArtworkDTO artworkDTO) {
        Artwork artwork = artworkMapper.toEntity(artworkDTO);
        Artwork createdArtwork = artworkService.createArtwork(artwork);
        return ResponseEntity.ok(artworkMapper.toDto(createdArtwork));
    }

    @GetMapping("/{artworkId}")
    public ResponseEntity<ArtworkDTO> getArtwork(@PathVariable Integer artworkId) {
        Artwork artwork = artworkService.getArtworkById(artworkId);
        return ResponseEntity.ok(artworkMapper.toDto(artwork));
    }

    @GetMapping("/user/{artist}")
    public ResponseEntity<List<ArtworkDTO>> getUserArtworks(
            @PathVariable String artist,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        List<Artwork> artworks = artworkService.getUserArtworks(artist, limit, offset);
        List<ArtworkDTO> artworkDTOs = artworks.stream()
                .map(artworkMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(artworkDTOs);
    }

    @GetMapping
    public ResponseEntity<List<ArtworkDTO>> getAllArtworks(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        List<Artwork> artworks = artworkService.getUserArtworks(null, limit, offset);
        List<ArtworkDTO> artworkDTOs = artworks.stream()
                .map(artworkMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(artworkDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArtworkDTO>> searchArtworks(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        List<Artwork> artworks = artworkService.searchArtworks(query, limit, offset);
        List<ArtworkDTO> artworkDTOs = artworks.stream()
                .map(artworkMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(artworkDTOs);
    }

    @PutMapping("/{artworkId}")
    public ResponseEntity<Void> updateArtwork(
            @PathVariable Integer artworkId,
            @RequestBody ArtworkDTO artworkDTO) {
        Artwork artwork = artworkMapper.toEntity(artworkDTO);
        artworkService.updateArtwork(artworkId, artwork);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{artworkId}")
    public ResponseEntity<Void> deleteArtwork(@PathVariable Integer artworkId) {
        artworkService.deleteArtwork(artworkId);
        return ResponseEntity.ok().build();
    }

}