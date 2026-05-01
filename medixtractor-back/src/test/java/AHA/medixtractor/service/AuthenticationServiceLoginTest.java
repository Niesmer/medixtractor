package AHA.medixtractor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import AHA.medixtractor.dto.AuthResponse;
import AHA.medixtractor.dto.LoginRequest;
import AHA.medixtractor.model.User;
import AHA.medixtractor.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InseeValidationService inseeValidationService;

    @InjectMocks
    private AuthenticationService service;

    @Test
    void loginUpdatesTokenWhenPasswordMatches() {
        User user = User.builder()
            .id(3L)
            .email("user@example.com")
            .password(new BCryptPasswordEncoder().encode("secret123"))
            .fullName("User Test")
            .role("ADMIN")
            .token("old-token")
            .build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        AuthResponse response = service.login(new LoginRequest("user@example.com", "secret123"));

        assertTrue(response.success());
        assertEquals("Login successful", response.message());
        assertNotEquals("old-token", response.token());
        assertEquals(response.token(), user.getToken());
        verify(userRepository).save(user);
    }

    @Test
    void loginRejectsWrongPassword() {
        User user = User.builder()
            .email("user@example.com")
            .password(new BCryptPasswordEncoder().encode("secret123"))
            .fullName("User Test")
            .role("ADMIN")
            .token("old-token")
            .build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.login(new LoginRequest("user@example.com", "bad-password"))
        );

        assertEquals("Invalid email or password", exception.getMessage());
    }
}
