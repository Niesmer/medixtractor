package AHA.medixtractor.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import AHA.medixtractor.config.BdpmProperties;

@Service
public class BdpmRemoteImportService {

    private static final String DEFAULT_SPECIALITES_URL =
        "https://base-donnees-publique.medicaments.gouv.fr/download/file/CIS_bdpm.txt";
    private static final String DEFAULT_PRESENTATIONS_URL =
        "https://base-donnees-publique.medicaments.gouv.fr/download/file/CIS_CIP_bdpm.txt";
    private static final String DEFAULT_COMPOSITIONS_URL =
        "https://base-donnees-publique.medicaments.gouv.fr/download/file/CIS_COMPO_bdpm.txt";
    private static final String DEFAULT_CACHE_DIR = "data/bdpm-cache";
    private static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 20;
    private static final int DEFAULT_REQUEST_TIMEOUT_SECONDS = 120;

    private final BdpmProperties bdpmProperties;

    public BdpmRemoteImportService(BdpmProperties bdpmProperties) {
        this.bdpmProperties = bdpmProperties;
    }

    public Path downloadBdpmFiles(boolean force) {
        BdpmProperties.Remote remote = bdpmProperties.remote();
        if (remote != null && !remote.enabled()) {
            throw new IllegalStateException("L'import BDPM distant est desactive dans la configuration.");
        }

        Path cacheDir = Path.of(resolveCacheDir(remote));
        try {
            Files.createDirectories(cacheDir);
        } catch (IOException exception) {
            throw new UncheckedIOException("Impossible de creer le dossier de cache BDPM : " + cacheDir, exception);
        }

        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(resolveConnectTimeout(remote)))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

        Map<String, String> filesToDownload = new LinkedHashMap<>();
        filesToDownload.put("CIS_bdpm.txt", resolveSpecialitesUrl(remote));
        filesToDownload.put("CIS_CIP_bdpm.txt", resolvePresentationsUrl(remote));
        filesToDownload.put("CIS_COMPO_bdpm.txt", resolveCompositionsUrl(remote));

        for (Map.Entry<String, String> entry : filesToDownload.entrySet()) {
            Path target = cacheDir.resolve(entry.getKey());
            if (!force && isUsableFile(target)) {
                continue;
            }
            downloadFile(client, entry.getValue(), target, resolveRequestTimeout(remote));
        }

        return cacheDir;
    }

    private void downloadFile(HttpClient client, String url, Path target, int requestTimeoutSeconds) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
            .timeout(Duration.ofSeconds(requestTimeoutSeconds))
            .header("User-Agent", "medixtractor/1.0")
            .GET()
            .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Telechargement BDPM impossible (" + response.statusCode() + ") : " + url);
            }

            try (InputStream body = response.body()) {
                Files.copy(body, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Telechargement BDPM impossible : " + url, exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Telechargement BDPM interrompu : " + url, exception);
        }
    }

    private boolean isUsableFile(Path path) {
        try {
            return Files.isRegularFile(path) && Files.size(path) > 0;
        } catch (IOException exception) {
            return false;
        }
    }

    private String resolveSpecialitesUrl(BdpmProperties.Remote remote) {
        return remote == null || isBlank(remote.specialitesUrl())
            ? DEFAULT_SPECIALITES_URL
            : remote.specialitesUrl();
    }

    private String resolvePresentationsUrl(BdpmProperties.Remote remote) {
        return remote == null || isBlank(remote.presentationsUrl())
            ? DEFAULT_PRESENTATIONS_URL
            : remote.presentationsUrl();
    }

    private String resolveCompositionsUrl(BdpmProperties.Remote remote) {
        return remote == null || isBlank(remote.compositionsUrl())
            ? DEFAULT_COMPOSITIONS_URL
            : remote.compositionsUrl();
    }

    private String resolveCacheDir(BdpmProperties.Remote remote) {
        return remote == null || isBlank(remote.cacheDir())
            ? DEFAULT_CACHE_DIR
            : remote.cacheDir();
    }

    private int resolveConnectTimeout(BdpmProperties.Remote remote) {
        return remote == null || remote.connectTimeoutSeconds() <= 0
            ? DEFAULT_CONNECT_TIMEOUT_SECONDS
            : remote.connectTimeoutSeconds();
    }

    private int resolveRequestTimeout(BdpmProperties.Remote remote) {
        return remote == null || remote.requestTimeoutSeconds() <= 0
            ? DEFAULT_REQUEST_TIMEOUT_SECONDS
            : remote.requestTimeoutSeconds();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
