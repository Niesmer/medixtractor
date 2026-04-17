package AHA.medixtractor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "medixtractor.bdpm")
public record BdpmProperties(String defaultSourceDir) {
}
