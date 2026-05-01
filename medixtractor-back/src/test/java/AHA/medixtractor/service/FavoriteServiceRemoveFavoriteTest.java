package AHA.medixtractor.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import AHA.medixtractor.model.User;
import AHA.medixtractor.repository.CompositionRepository;
import AHA.medixtractor.repository.FavoriteRepository;
import AHA.medixtractor.repository.MedicamentRepository;
import AHA.medixtractor.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceRemoveFavoriteTest {

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
    void removeFavoriteDeletesFavoriteForTokenUser() {
        User user = User.builder().id(9L).token("token-123").build();
        when(userRepository.findByToken("token-123")).thenReturn(Optional.of(user));

        service.removeFavorite("Bearer token-123", 12345678L);

        verify(favoriteRepository).deleteByUserIdAndCis(9L, 12345678L);
    }
}
