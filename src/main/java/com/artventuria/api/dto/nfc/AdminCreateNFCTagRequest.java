package com.artventuria.api.dto.nfc;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateNFCTagRequest {
    @NotBlank
    private String token;
    
    @NotNull
    private Integer artwork_id;
    
    private boolean active = true;
}