package AHA.medixtractor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import AHA.medixtractor.dto.AuthResponse;
import AHA.medixtractor.dto.SignupRequest;
import AHA.medixtractor.model.User;
import AHA.medixtractor.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceSignUpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InseeValidationService inseeValidationService;

    @InjectMocks
    private AuthenticationService service;

    @Test
    void signUpCreatesUserWithHashedPasswordAndToken() {
        SignupRequest request = new SignupRequest("User Test", "user@example.com", "secret123", "ADMIN", null);
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(7L);
            return user;
        });

        AuthResponse response = service.signUp(request);

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertTrue(response.success());
        assertEquals("Account created successfully", response.message());
        assertNotNull(response.token());
        assertEquals("user@example.com", savedUser.getValue().getEmail());
        assertTrue(new BCryptPasswordEncoder().matches("secret123", savedUser.getValue().getPassword()));
    }

    @Test
    void signUpRejectsDuplicateEmail() {
        SignupRequest request = new SignupRequest("User Test", "user@example.com", "secret123", "ADMIN", null);
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.signUp(request));

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signUpRejectsShortPassword() {
        SignupRequest request = new SignupRequest("User Test", "user@example.com", "short", "ADMIN", null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.signUp(request));

        assertEquals("Password must be at least 6 characters", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
