package codes.dirty.example.spatialignite.model;

import lombok.Builder;
import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

@Data
@Builder
public class GeodataKey {

    @QuerySqlField(index = true, notNull = true)
    private Double longitude;  // -151

    @QuerySqlField(index = true, notNull = true)
    private Double latitude;  // 38

    @QuerySqlField(index = true, notNull = true)
    private String businessId;
}