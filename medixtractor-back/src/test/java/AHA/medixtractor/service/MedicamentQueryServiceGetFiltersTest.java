package AHA.medixtractor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import AHA.medixtractor.dto.SearchFiltersDto;
import AHA.medixtractor.repository.CompositionRepository;
import AHA.medixtractor.repository.MedicamentRepository;
import AHA.medixtractor.repository.PresentationRepository;

@ExtendWith(MockitoExtension.class)
class MedicamentQueryServiceGetFiltersTest {

    @Mock
    private MedicamentRepository medicamentRepository;

    @Mock
    private CompositionRepository compositionRepository;

    @Mock
    private PresentationRepository presentationRepository;

    @InjectMocks
    private MedicamentQueryService service;

    @Test
    void getFiltersReturnsDistinctRepositoryValues() {
        when(compositionRepository.findDistinctSubstances()).thenReturn(List.of("IBUPROFENE", "PARACETAMOL"));
        when(medicamentRepository.findDistinctFormes()).thenReturn(List.of("comprime"));
        when(medicamentRepository.findDistinctStatuts()).thenReturn(List.of("AMM"));
        when(medicamentRepository.findDistinctLaboratoires()).thenReturn(List.of("SANOFI"));

        SearchFiltersDto result = service.getFilters();

        assertEquals(List.of("IBUPROFENE", "PARACETAMOL"), result.substances());
        assertEquals(List.of("comprime"), result.formes());
        assertEquals(List.of("AMM"), result.statuts());
        assertEquals(List.of("SANOFI"), result.laboratoires());
    }
}
