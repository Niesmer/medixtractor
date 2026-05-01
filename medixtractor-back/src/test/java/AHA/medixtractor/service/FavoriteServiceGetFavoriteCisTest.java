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

import AHA.medixtractor.model.Favorite;
import AHA.medixtractor.model.User;
import AHA.medixtractor.repository.CompositionRepository;
import AHA.medixtractor.repository.FavoriteRepository;
import AHA.medixtractor.repository.MedicamentRepository;
import AHA.medixtractor.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceGetFavoriteCisTest {

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
    void getFavoriteCisReturnsOrderedCisValuesForTokenUser() {
        User user = User.builder().id(5L).token("token-123").build();
        when(userRepository.findByToken("token-123")).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUserIdOrderByCisAsc(5L)).thenReturn(List.of(
            Favorite.builder().userId(5L).cis(100L).build(),
            Favorite.builder().userId(5L).cis(200L).build()
        ));

        List<Long> result = service.getFavoriteCis("Bearer token-123");

        assertEquals(List.of(100L, 200L), result);
    }
}
