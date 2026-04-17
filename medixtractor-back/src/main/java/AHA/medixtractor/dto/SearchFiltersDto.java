package AHA.medixtractor.dto;

import java.util.List;

public record SearchFiltersDto(
    List<String> substances,
    List<String> formes,
    List<String> statuts,
    List<String> laboratoires
) {
}
