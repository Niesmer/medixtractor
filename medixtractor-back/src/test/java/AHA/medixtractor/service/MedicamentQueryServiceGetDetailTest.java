package AHA.medixtractor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import AHA.medixtractor.dto.MedicamentDetailView;
import AHA.medixtractor.model.Composition;
import AHA.medixtractor.model.Medicament;
import AHA.medixtractor.model.Presentation;
import AHA.medixtractor.repository.CompositionRepository;
import AHA.medixtractor.repository.MedicamentRepository;
import AHA.medixtractor.repository.PresentationRepository;

@ExtendWith(MockitoExtension.class)
class MedicamentQueryServiceGetDetailTest {

    @Mock
    private MedicamentRepository medicamentRepository;

    @Mock
    private CompositionRepository compositionRepository;

    @Mock
    private PresentationRepository presentationRepository;

    @InjectMocks
    private MedicamentQueryService service;

    @Test
    void getDetailReturnsMedicamentWithCompositionsAndPresentations() {
        when(medicamentRepository.findById(12345678L)).thenReturn(Optional.of(Medicament.builder()
            .cis(12345678L)
            .nom("DOLIPRANE")
            .laboratoire("SANOFI")
            .build()));
        when(compositionRepository.findByCisOrderBySubstanceAsc(12345678L)).thenReturn(List.of(
            Composition.builder().cis(12345678L).substance("PARACETAMOL").dosage(1000.0).unite("mg").build()
        ));
        when(presentationRepository.findByCisOrderByCipAsc(12345678L)).thenReturn(List.of(
            Presentation.builder().cis(12345678L).cip("1111111").prix(2.18).remboursement("65%").build()
        ));

        MedicamentDetailView result = service.getDetail(12345678L);

        assertEquals(12345678L, result.cis());
        assertEquals("DOLIPRANE", result.name());
        assertEquals(List.of("PARACETAMOL"), result.activeSubstances());
        assertEquals("1111111", result.presentations().getFirst().cip());
    }

    @Test
    void getDetailRejectsUnknownCis() {
        when(medicamentRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.getDetail(999L));

        assertEquals("Medicament introuvable pour le CIS 999", exception.getMessage());
    }
}
