package codes.dirty.example.spatialignite.controller;

import codes.dirty.example.spatialignite.controller.dto.GeoJsonGeodata;
import codes.dirty.example.spatialignite.model.Geodata;
import codes.dirty.example.spatialignite.repository.GeodataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GeodataController {

    private final GeodataRepository geodataRepository;

    public GeodataController(GeodataRepository geodataRepository) {
        this.geodataRepository = geodataRepository;
    }

    @GetMapping("/geodata")
    public ResponseEntity<List<Geodata>> queryAll() {
        return ResponseEntity.ok(geodataRepository.queryAll());
    }

    @PostMapping("/geodata")
    public ResponseEntity<String> saveAll(@RequestBody List<GeoJsonGeodata> geoJsonGeodata) {
        geodataRepository.saveAll(geoJsonGeodata.stream()
                .map(GeoJsonGeodata::toGeodata)
                .collect(Collectors.toList()));
        return ResponseEntity.ok("ok");
    }
}
