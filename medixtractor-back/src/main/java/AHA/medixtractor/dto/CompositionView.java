package AHA.medixtractor.dto;

import AHA.medixtractor.model.Composition;

public record CompositionView(
    Long id,
    String substance,
    String dosage
) {
    public static CompositionView fromEntity(Composition composition) {
        String dosage = composition.getDosage() == null
            ? null
            : formatNumber(composition.getDosage()) + (composition.getUnite() == null ? "" : " " + composition.getUnite());
        return new CompositionView(composition.getId(), composition.getSubstance(), dosage);
    }

    private static String formatNumber(Double value) {
        if (value == null) {
            return null;
        }
        if (Math.floor(value) == value) {
            return Long.toString(value.longValue());
        }
        return value.toString();
    }
}
