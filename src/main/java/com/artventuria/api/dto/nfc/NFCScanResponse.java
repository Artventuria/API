package com.artventuria.api.dto.nfc;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NFCScanResponse {
    private Long artworkId;
    private String status;
    private Long tagId;
    private String token;
    private String location;
    private String deviceId;
    private Integer userId;
    private Integer pointsEarned;

    // Constructor without the pointsEarned field for compatibility with existing
    // code
    public NFCScanResponse(Long artworkId, String status, Long tagId, String token,
            String location, String deviceId, Integer userId) {
        this.artworkId = artworkId;
        this.status = status;
        this.tagId = tagId;
        this.token = token;
        this.location = location;
        this.deviceId = deviceId;
        this.userId = userId;
        this.pointsEarned = null;
    }
}