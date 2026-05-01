package AHA.medixtractor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import AHA.medixtractor.model.Favorite;
import AHA.medixtractor.model.Medicament;
import AHA.medixtractor.model.User;
import AHA.medixtractor.repository.CompositionRepository;
import AHA.medixtractor.repository.FavoriteRepository;
import AHA.medixtractor.repository.MedicamentRepository;
import AHA.medixtractor.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceAddFavoriteTest {

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
    void addFavoriteSavesFavoriteWhenMedicamentExists() {
        User user = User.builder().id(9L).token("token-123").build();
        when(userRepository.findByToken("token-123")).thenReturn(Optional.of(user));
        when(medicamentRepository.findById(12345678L)).thenReturn(Optional.of(Medicament.builder().cis(12345678L).build()));
        when(favoriteRepository.existsByUserIdAndCis(9L, 12345678L)).thenReturn(false);

        service.addFavorite("Bearer token-123", 12345678L);

        ArgumentCaptor<Favorite> savedFavorite = ArgumentCaptor.forClass(Favorite.class);
        verify(favoriteRepository).save(savedFavorite.capture());
        assertEquals(9L, savedFavorite.getValue().getUserId());
        assertEquals(12345678L, savedFavorite.getValue().getCis());
    }

    @Test
    void addFavoriteDoesNotSaveDuplicateFavorite() {
        User user = User.builder().id(9L).token("token-123").build();
        when(userRepository.findByToken("token-123")).thenReturn(Optional.of(user));
        when(medicamentRepository.findById(12345678L)).thenReturn(Optional.of(Medicament.builder().cis(12345678L).build()));
        when(favoriteRepository.existsByUserIdAndCis(9L, 12345678L)).thenReturn(true);

        service.addFavorite("Bearer token-123", 12345678L);

        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void addFavoriteRejectsMissingMedicament() {
        User user = User.builder().id(9L).token("token-123").build();
        when(userRepository.findByToken("token-123")).thenReturn(Optional.of(user));
        when(medicamentRepository.findById(12345678L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.addFavorite("Bearer token-123", 12345678L)
        );

        assertEquals("Medicament introuvable pour le CIS 12345678", exception.getMessage());
    }
}
