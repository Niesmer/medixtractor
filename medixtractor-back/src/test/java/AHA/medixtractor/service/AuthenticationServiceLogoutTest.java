package AHA.medixtractor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import AHA.medixtractor.model.User;
import AHA.medixtractor.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceLogoutTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InseeValidationService inseeValidationService;

    @InjectMocks
    private AuthenticationService service;

    @Test
    void logoutClearsToken() {
        User user = User.builder().token("token-123").build();
        when(userRepository.findByToken("token-123")).thenReturn(Optional.of(user));

        service.logout("token-123");

        assertNull(user.getToken());
        verify(userRepository).save(user);
    }

    @Test
    void logoutRejectsUnknownToken() {
        when(userRepository.findByToken("bad-token")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.logout("bad-token"));

        assertEquals("Invalid token", exception.getMessage());
    }
}
