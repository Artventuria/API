package com.artventuria.api.dto.artwork;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkMetadataDTO {
    private String id;
    private Integer artwork_id;
    private String description_extended;
    private String historical_context;
    private List<String> materials;
    private DimensionsDTO dimensions;
    private List<String> tags;
    private List<String> external_links;
    private boolean temporary_exhibition;
    private Object additional_details;
    private Instant created_at;
    private Instant updated_at;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DimensionsDTO {
        private Double depth;
        private Double height;
        private String unit;
        private Double width;
    }
}