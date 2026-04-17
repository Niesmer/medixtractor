package AHA.medixtractor.dto;

import java.util.List;

public record MedicamentDetailView(
    Long id,
    Long cis,
    String name,
    String pharmaceuticalForm,
    String administrationRoute,
    String status,
    String procedure,
    String commercialization,
    String marketingDate,
    String laboratory,
    List<String> activeSubstances,
    List<CompositionView> compositions,
    List<PresentationView> presentations
) {
}
