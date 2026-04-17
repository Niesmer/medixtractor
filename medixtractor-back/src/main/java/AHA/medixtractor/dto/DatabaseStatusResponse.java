package AHA.medixtractor.dto;

public record DatabaseStatusResponse(
    long medicaments,
    long presentations,
    long compositions
) {
}
