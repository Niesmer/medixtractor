package AHA.medixtractor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import AHA.medixtractor.dto.MedicamentSummaryView;
import AHA.medixtractor.model.Composition;
import AHA.medixtractor.model.Favorite;
import AHA.medixtractor.model.Medicament;
import AHA.medixtractor.model.User;
import AHA.medixtractor.repository.CompositionRepository;
import AHA.medixtractor.repository.FavoriteRepository;
import AHA.medixtractor.repository.MedicamentRepository;
import AHA.medixtractor.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceGetFavoritesTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MedicamentRepository medicamentRepository;

    @Mock
    private CompositionRepository compositionRepository;

    @InjectMocks
    private FavoriteService service;

    @Test
    void getFavoritesReturnsSummariesWithUniqueSubstances() {
        User user = User.builder().id(9L).token("token-123").build();
        when(userRepository.findByToken("token-123")).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUserIdOrderByCisAsc(9L)).thenReturn(List.of(Favorite.builder().cis(12345678L).build()));
        when(medicamentRepository.findAllById(List.of(12345678L))).thenReturn(List.of(Medicament.builder()
            .cis(12345678L)
            .nom("DOLIPRANE")
            .forme("comprime")
            .voie("orale")
            .statut("AMM")
            .laboratoire("SANOFI")
            .dateAmm("01/01/2020")
            .build()));
        when(compositionRepository.findByCisInOrderByCisAscSubstanceAsc(List.of(12345678L))).thenReturn(List.of(
            Composition.builder().cis(12345678L).substance("PARACETAMOL").build(),
            Composition.builder().cis(12345678L).substance("PARACETAMOL").build()
        ));

        List<MedicamentSummaryView> result = service.getFavorites("Bearer token-123");

        assertEquals(1, result.size());
        assertEquals(12345678L, result.getFirst().cis());
        assertEquals("DOLIPRANE", result.getFirst().name());
        assertEquals(List.of("PARACETAMOL"), result.getFirst().activeSubstances());
    }
}
