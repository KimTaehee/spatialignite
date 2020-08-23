package codes.dirty.example.spatialignite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Geodata {

    @QuerySqlField(notNull = true)
    private String geometryType;

    @QuerySqlField(index = true, notNull = true)
    private Double longitude;  // -151

    @QuerySqlField(index = true, notNull = true)
    private Double latitude;  // 38

    @QuerySqlField(index = true, notNull = true)
    private String businessId;

    @QuerySqlField(notNull = true)
    private String name;

    @QuerySqlField
    private Double stars;

    @QuerySqlField
    private Long reviewCount;

    @QuerySqlField
    private String categories;
    
    @QuerySqlField(notNull = true)
    private String mongoId;

    @JsonIgnore
    public GeodataKey getKey() {
        return GeodataKey.builder()
                .longitude(longitude)
                .latitude(latitude)
                .businessId(businessId)
                .build();
    }
}