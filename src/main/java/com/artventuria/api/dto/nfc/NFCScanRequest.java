package com.artventuria.api.dto.nfc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NFCScanRequest {
    @NotBlank
    private String token;

    private Long userId;

    private String location;

    @JsonProperty("device_id")
    private String deviceId;
}