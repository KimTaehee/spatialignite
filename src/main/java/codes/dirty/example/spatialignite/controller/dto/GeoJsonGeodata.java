package codes.dirty.example.spatialignite.controller.dto;

import codes.dirty.example.spatialignite.model.Geodata;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

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
        final WKTReader r = new WKTReader();
        try {
            return Geodata.builder()
                    .geometryType(geometry.getType())
                    .point((Point) new WKTReader().read("POINT(" +
                            geometry.getCoordinates()[0] +
                            " " +
                            geometry.getCoordinates()[1] + ")"))
                    .businessId(properties.getBusinessId())
                    .name(properties.getName())
                    .stars(properties.getStars())
                    .reviewCount(properties.getReviewCount())
                    .categories(properties.getCategories())
                    .mongoId(mongoId)
                    .build();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @NoArgsConstructor
    @Data
    static class Geometry {
        private String type;
        /**
         * [0] is longitude, [1] is latitude
         */
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