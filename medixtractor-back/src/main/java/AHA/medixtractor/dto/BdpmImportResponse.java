package AHA.medixtractor.dto;

public record BdpmImportResponse(
    boolean succes,
    String message,
    String sourceDir,
    int medicamentsImported,
    int presentationsImported,
    int compositionsImported,
    long medicamentsEnBase,
    long presentationsEnBase,
    long compositionsEnBase,
    ImportFileReport fichierMedicaments,
    ImportFileReport fichierPresentations,
    ImportFileReport fichierCompositions
) {
}
