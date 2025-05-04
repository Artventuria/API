package com.artventuria.api.dto.nfc;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NFCScanAnalytics {
    private Long totalScans;
    private Long uniqueUsers;
    private Long totalPoints;
    private Double averagePointsPerScan;
    private String timeRange;
    private String location;
}