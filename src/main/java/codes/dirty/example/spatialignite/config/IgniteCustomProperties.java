package codes.dirty.example.spatialignite.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ignite-custom")
@Data
public class IgniteCustomProperties {
    private int backup = 1;
}