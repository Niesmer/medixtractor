package AHA.medixtractor.dto;

import AHA.medixtractor.model.Presentation;

public record PresentationView(
    String cip,
    String prix,
    String remboursement
) {
    public static PresentationView fromEntity(Presentation presentation) {
        String prix = presentation.getPrix() == null ? null : formatPrice(presentation.getPrix());
        return new PresentationView(presentation.getCip(), prix, presentation.getRemboursement());
    }

    private static String formatPrice(Double prix) {
        return (Math.floor(prix) == prix ? Long.toString(prix.longValue()) : prix.toString()) + " EUR";
    }
}
