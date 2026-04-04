package AHA.medixtractor.controller;

import java.nio.file.Path;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import AHA.medixtractor.config.BdpmProperties;
import AHA.medixtractor.dto.BdpmImportResponse;
import AHA.medixtractor.dto.DatabaseStatusResponse;
import AHA.medixtractor.dto.StartupImportStatusResponse;
import AHA.medixtractor.service.BdpmImportService;
import AHA.medixtractor.service.StartupImportService;

@RestController
@RequestMapping("/api/imports")
public class BdpmImportController {

    private final BdpmImportService bdpmImportService;
    private final BdpmProperties bdpmProperties;
    private final StartupImportService startupImportService;

    public BdpmImportController(
        BdpmImportService bdpmImportService,
        BdpmProperties bdpmProperties,
        StartupImportService startupImportService
    ) {
        this.bdpmImportService = bdpmImportService;
        this.bdpmProperties = bdpmProperties;
        this.startupImportService = startupImportService;
    }

    @PostMapping("/bdpm")
    @ResponseStatus(HttpStatus.CREATED)
    public BdpmImportResponse importBdpm(@RequestParam(required = false) String sourceDir) {
        String effectiveSourceDir = sourceDir == null || sourceDir.isBlank()
            ? bdpmProperties.defaultSourceDir()
            : sourceDir.trim();
        return bdpmImportService.importFromDirectory(Path.of(effectiveSourceDir));
    }

    @GetMapping("/statut")
    public DatabaseStatusResponse getDatabaseStatus() {
        return bdpmImportService.getDatabaseStatus();
    }

    @GetMapping("/demarrage")
    public StartupImportStatusResponse getStartupImportStatus() {
        return startupImportService.getStatus();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRuntimeException(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return "Import impossible : une erreur technique s'est produite pendant la lecture des fichiers.";
        }
        return "Import impossible : " + message;
    }
}
