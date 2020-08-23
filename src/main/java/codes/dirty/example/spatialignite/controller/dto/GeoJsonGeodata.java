package codes.dirty.example.spatialignite.controller.dto;

import codes.dirty.example.spatialignite.model.Geodata;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeoJsonGeodata {

    @JsonProperty("_id")
    private String mongoId;

    private Geometry geometry;

    private Properties properties;

    public Geodata toGeodata() {
        return Geodata.builder()
                .geometryType(geometry.getType())
                .longitude(geometry.getCoordinates()[0])
                .latitude(geometry.getCoordinates()[1])
                .businessId(properties.getBusinessId())
                .name(properties.getName())
                .stars(properties.getStars())
                .reviewCount(properties.getReviewCount())
                .categories(properties.getCategories())
                .mongoId(mongoId)
                .build();
    }

    @NoArgsConstructor
    @Data
    static class Geometry {
        private String type;
        private Double[] coordinates;
    }

    @NoArgsConstructor
    @Data
    static class Properties {

        @JsonProperty("business_id")
        private String businessId;
        private String name;
        private Double stars;
        @JsonProperty("review_count")
        private Long reviewCount;
        private String categories;
    }
}