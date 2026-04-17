package AHA.medixtractor.service;

import AHA.medixtractor.dto.*;
import AHA.medixtractor.model.User;
import AHA.medixtractor.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final InseeValidationService inseeValidationService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, InseeValidationService inseeValidationService) {
        this.userRepository = userRepository;
        this.inseeValidationService = inseeValidationService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public AuthResponse signUp(SignupRequest request) {
        // Validate email not already in use
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Validate password
        if (request.password() == null || request.password().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        // Validate SIRET/SIREN for DOCTOR and PHARMACIST roles
        if (request.role().equals("DOCTOR") || request.role().equals("PHARMACIST")) {
            InseeValidationService.InseeValidationResult validation =
                inseeValidationService.validateSiretSiren(request.siretSiren(), request.role());
            if (!validation.valid()) {
                throw new IllegalArgumentException(validation.message());
            }
        }

        // Create user with hashed password and UUID token
        String hashedPassword = passwordEncoder.encode(request.password());
        String token = UUID.randomUUID().toString();

        User newUser = User.builder()
            .email(request.email())
            .password(hashedPassword)
            .fullName(request.fullName())
            .role(request.role())
            .siretSiren(request.siretSiren())
            .token(token)
            .build();

        User savedUser = userRepository.save(newUser);

        return new AuthResponse(
            true,
            "Account created successfully",
            token,
            UserView.fromEntity(savedUser)
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate new token
        String newToken = UUID.randomUUID().toString();
        user.setToken(newToken);
        User updatedUser = userRepository.save(user);

        return new AuthResponse(
            true,
            "Login successful",
            newToken,
            UserView.fromEntity(updatedUser)
        );
    }

    public void logout(String token) {
        User user = userRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        user.setToken(null);
        userRepository.save(user);
    }
}
