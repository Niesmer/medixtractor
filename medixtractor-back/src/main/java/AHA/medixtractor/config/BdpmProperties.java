package AHA.medixtractor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "medixtractor.bdpm")
public record BdpmProperties(String defaultSourceDir, Remote remote) {

    public record Remote(
        boolean enabled,
        String specialitesUrl,
        String presentationsUrl,
        String compositionsUrl,
        String cacheDir,
        int connectTimeoutSeconds,
        int requestTimeoutSeconds
    ) {
    }
}
