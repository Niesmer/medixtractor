package AHA.medixtractor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "insee.api")
public record InseeProperties(String key, String url) {}
