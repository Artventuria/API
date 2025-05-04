package com.artventuria.api.dto.nfc;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNFCTagRequest {
    @NotBlank
    private String token;
    
    @NotBlank
    private String name;
    
    private String description;
    
    private Long artworkId;
    
    private boolean active = true;
}