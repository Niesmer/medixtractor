package AHA.medixtractor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import AHA.medixtractor.dto.MedicamentSummaryView;
import AHA.medixtractor.model.Composition;
import AHA.medixtractor.model.Medicament;
import AHA.medixtractor.repository.CompositionRepository;
import AHA.medixtractor.repository.MedicamentRepository;
import AHA.medixtractor.repository.PresentationRepository;

@ExtendWith(MockitoExtension.class)
class MedicamentQueryServiceSearchTest {

    @Mock
    private MedicamentRepository medicamentRepository;

    @Mock
    private CompositionRepository compositionRepository;

    @Mock
    private PresentationRepository presentationRepository;

    @InjectMocks
    private MedicamentQueryService service;

    @Test
    void searchNormalizesFiltersAndReturnsSubstanceSummaries() {
        Medicament medicament = Medicament.builder().cis(12345678L).nom("DOLIPRANE").forme("comprime").build();
        when(medicamentRepository.search("doliprane", null, "comprime", null, null, null)).thenReturn(List.of(medicament));
        when(compositionRepository.findByCisInOrderByCisAscSubstanceAsc(List.of(12345678L))).thenReturn(List.of(
            Composition.builder().cis(12345678L).substance("PARACETAMOL").build()
        ));

        List<MedicamentSummaryView> result = service.search(" doliprane ", " ", " comprime ", null, null, null);

        assertEquals(1, result.size());
        assertEquals(12345678L, result.getFirst().cis());
        assertEquals(List.of("PARACETAMOL"), result.getFirst().activeSubstances());
        verify(medicamentRepository).search("doliprane", null, "comprime", null, null, null);
    }
}
