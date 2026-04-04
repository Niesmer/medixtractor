package AHA.medixtractor.dto;

public record StartupImportStatusResponse(
    boolean tentativeEffectuee,
    boolean succes,
    String message,
    String sourceDir
) {
}
