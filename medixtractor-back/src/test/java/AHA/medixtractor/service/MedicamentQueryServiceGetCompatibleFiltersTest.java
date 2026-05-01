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

import AHA.medixtractor.dto.SearchFiltersDto;
import AHA.medixtractor.repository.CompositionRepository;
import AHA.medixtractor.repository.MedicamentRepository;
import AHA.medixtractor.repository.PresentationRepository;

@ExtendWith(MockitoExtension.class)
class MedicamentQueryServiceGetCompatibleFiltersTest {

    @Mock
    private MedicamentRepository medicamentRepository;

    @Mock
    private CompositionRepository compositionRepository;

    @Mock
    private PresentationRepository presentationRepository;

    @InjectMocks
    private MedicamentQueryService service;

    @Test
    void getCompatibleFiltersNormalizesInputsAndReturnsRepositoryValues() {
        when(compositionRepository.findCompatibleSubstances("doli", "comprime", null, "oui", "SANOFI"))
            .thenReturn(List.of("PARACETAMOL"));
        when(medicamentRepository.findCompatibleFormes("doli", "PARACETAMOL", null, "oui", "SANOFI"))
            .thenReturn(List.of("comprime"));
        when(medicamentRepository.findCompatibleStatuts("doli", "PARACETAMOL", "comprime", "oui", "SANOFI"))
            .thenReturn(List.of("AMM"));
        when(medicamentRepository.findCompatibleLaboratoires("doli", "PARACETAMOL", "comprime", null, "oui"))
            .thenReturn(List.of("SANOFI"));

        SearchFiltersDto result = service.getCompatibleFilters(" doli ", " PARACETAMOL ", " comprime ", " ", " oui ", " SANOFI ");

        assertEquals(List.of("PARACETAMOL"), result.substances());
        assertEquals(List.of("comprime"), result.formes());
        assertEquals(List.of("AMM"), result.statuts());
        assertEquals(List.of("SANOFI"), result.laboratoires());
        verify(compositionRepository).findCompatibleSubstances("doli", "comprime", null, "oui", "SANOFI");
    }
}
