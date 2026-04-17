package AHA.medixtractor.dto;

public record ImportFileReport(
    String fichier,
    int lignesLues,
    int lignesImportees,
    int lignesIgnorees,
    int lignesInvalides
) {
}
