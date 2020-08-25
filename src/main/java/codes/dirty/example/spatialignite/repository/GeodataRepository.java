package codes.dirty.example.spatialignite.repository;

import codes.dirty.example.spatialignite.model.Geodata;
import codes.dirty.example.spatialignite.model.GeodataKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.cache.Cache;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class GeodataRepository {

    private static final String CACHE_NAME = "GEODATA_CACHE";
    private static final String TABLE_NAME = CACHE_NAME + ".GEODATA";

    private final ClientCache<GeodataKey, Geodata> clientCache;

    public GeodataRepository(IgniteClient igniteClient) {
        this.clientCache = igniteClient.cache(CACHE_NAME);
    }

    public void save(@NonNull Geodata geodata) {
        clientCache.put(geodata.getKey(), geodata);
    }

    public void saveAll(@NonNull List<Geodata> geodata) {
        Set<GeodataKey> seen = ConcurrentHashMap.newKeySet();
        Map<GeodataKey, Geodata> map = geodata.stream()
                .filter(distinctByKey(Geodata::getKey, seen))
                .collect(Collectors.toMap(Geodata::getKey,
                        Function.identity()));
        // TODO: Consider using putAllIfAbsent or update clause because there is a 'createdAt' timestamp.
        clientCache.putAll(map);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, GeodataKey> keyExtractor, Set<GeodataKey> seen) {
        return t -> seen.add(keyExtractor.apply(t));
    }

//    public List<Geodata> findAll(Integer limit) {
//        if (limit == null) {
//            limit = ScanQuery.DFLT_PAGE_SIZE;
//        }
//        final QueryCursor<Entry<GeodataKey, Geodata>> cursor
//                = clientCache.query(new ScanQuery<GeodataKey, Geodata>()
//                .setPageSize(limit));  // TODO: not work. fix it.
//        return cursor.getAll()
//                .stream()
//                .map(Entry::getValue)
//                .collect(Collectors.toList());
//    }

    public Geodata findByKey(@NonNull GeodataKey key) {
        return clientCache.get(key);
    }

//    @NonNull
//    public List<Geodata> queryAll() {
//        String sql = "SELECT geometryType, point, businessId, name, stars, categories FROM " + TABLE_NAME;
//        log.debug("sql: {}", sql);
//        QueryCursor<List<?>> cursor = clientCache.query(new SqlFieldsQuery(sql));
//        return StreamSupport.stream(cursor.spliterator(), false)
//                .map(row -> {
//                    log.debug("Row: " + row);
//                    return Geodata.builder()
//                            .geometryType((String) row.get(0))
//                            .point((Point) row.get(1))
//                            .businessId((String) row.get(2))
//                            .name((String) row.get(3))
//                            .stars((Double) row.get(4))
//                            .categories((String) row.get(5))
//                            .build();
//                })
//                .collect(Collectors.toList());
//    }  // TODO: This cannot deserialize because of JTS Point type.

    @NonNull
    public List<Geodata> queryAll() {
        // Query to find points that fit into a polygon.
        // TODO: change it with SqlFieldQuery
        SqlQuery<GeodataKey, Geodata> query = new SqlQuery<>(Geodata.class, "point && ?");

        // Defining the polygon's boundaries.
        query.setArgs("POLYGON((-180 -90, -180 90, 180 90, 180 -90, -180 -90))");

        // Executing the query.
        List<Cache.Entry<GeodataKey, Geodata>> entries = clientCache.query(query).getAll();

        // Return points that fit into the area defined by the polygon.
        return entries.stream()
                .map(Cache.Entry::getValue)
                .collect(Collectors.toList());
    }
}