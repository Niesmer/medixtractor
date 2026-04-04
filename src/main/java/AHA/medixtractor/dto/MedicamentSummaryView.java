package AHA.medixtractor.dto;

import java.util.List;

public record MedicamentSummaryView(
    Long id,
    Long cis,
    String name,
    String pharmaceuticalForm,
    String administrationRoute,
    String status,
    String laboratory,
    List<String> activeSubstances,
    String marketingDate
) {
}
