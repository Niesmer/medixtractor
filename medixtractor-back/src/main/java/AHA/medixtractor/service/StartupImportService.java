package AHA.medixtractor.service;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import AHA.medixtractor.config.BdpmProperties;
import AHA.medixtractor.dto.StartupImportStatusResponse;

@Service
public class StartupImportService implements ApplicationRunner {

    private final BdpmImportService bdpmImportService;
    private final BdpmProperties bdpmProperties;

    private volatile StartupImportStatusResponse status = new StartupImportStatusResponse(
        false,
        false,
        "Aucun import automatique n'a encore ete tente.",
        null
    );

    public StartupImportService(BdpmImportService bdpmImportService, BdpmProperties bdpmProperties) {
        this.bdpmImportService = bdpmImportService;
        this.bdpmProperties = bdpmProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (bdpmProperties.defaultSourceDir() == null || bdpmProperties.defaultSourceDir().isBlank()) {
            status = new StartupImportStatusResponse(
                true,
                false,
                "Import automatique ignore : aucun dossier source BDPM n'est configure.",
                null
            );
            return;
        }

        if (bdpmImportService.getDatabaseStatus().medicaments() > 0) {
            status = new StartupImportStatusResponse(
                true,
                true,
                "Import automatique ignore : la base contient deja des donnees.",
                bdpmProperties.defaultSourceDir()
            );
            return;
        }

        Path sourceDir = Path.of(bdpmProperties.defaultSourceDir());
        if (!Files.isDirectory(sourceDir)) {
            status = new StartupImportStatusResponse(
                true,
                false,
                "Import automatique ignore : le dossier source BDPM est introuvable.",
                sourceDir.toString()
            );
            return;
        }

        try {
            var response = bdpmImportService.importFromDirectory(sourceDir);
            status = new StartupImportStatusResponse(
                true,
                response.succes(),
                response.message(),
                response.sourceDir()
            );
        } catch (RuntimeException exception) {
            status = new StartupImportStatusResponse(
                true,
                false,
                "Import automatique impossible : " + safeMessage(exception),
                sourceDir.toString()
            );
        }
    }

    public StartupImportStatusResponse getStatus() {
        return status;
    }

    private String safeMessage(RuntimeException exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank()
            ? "une erreur technique s'est produite pendant l'import."
            : message;
    }
}
