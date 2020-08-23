package codes.dirty.example.spatialignite.repository;

import codes.dirty.example.spatialignite.model.Geodata;
import codes.dirty.example.spatialignite.model.GeodataKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    @NonNull
    public List<Geodata> queryAll() {
        String sql = "SELECT geometryType, longitude, latitude, businessId, name, stars, categories FROM " + TABLE_NAME;
        log.debug("sql: {}", sql);
        QueryCursor<List<?>> cursor = clientCache.query(new SqlFieldsQuery(sql));
        return StreamSupport.stream(cursor.spliterator(), false)
                .map(row -> {
                    log.debug("Row: " + row);
                    return Geodata.builder()
                            .geometryType((String) row.get(0))
                            .longitude((Double) row.get(1))
                            .latitude((Double) row.get(2))
                            .businessId((String) row.get(3))
                            .name((String) row.get(4))
                            .stars((Double) row.get(5))
                            .categories((String) row.get(6))
                            .build();
                })
                .collect(Collectors.toList());
    }
}