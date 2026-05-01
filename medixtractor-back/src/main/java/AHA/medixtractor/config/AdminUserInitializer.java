package AHA.medixtractor.config;

import AHA.medixtractor.model.User;
import AHA.medixtractor.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@Configuration
public class AdminUserInitializer {

    @Bean
    public ApplicationRunner initializeDefaultUsers(UserRepository userRepository) {
        return args -> {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // Create admin user if not exists
            if (!userRepository.existsByEmail("admin@medixtractor.local")) {
                String hashedPassword = passwordEncoder.encode("AdminPassword123!");
                String token = UUID.randomUUID().toString();

                User adminUser = User.builder()
                    .email("admin@medixtractor.local")
                    .password(hashedPassword)
                    .fullName("Administrator")
                    .role("ADMIN")
                    .token(token)
                    .build();

                userRepository.save(adminUser);
                System.out.println("✓ Admin user created - Email: admin@medixtractor.local | Password: AdminPassword123!");
            }

            // Create doctor user if not exists
            if (!userRepository.existsByEmail("doctor@medixtractor.local")) {
                String hashedPassword = passwordEncoder.encode("DoctorPassword123!");
                String token = UUID.randomUUID().toString();

                User doctorUser = User.builder()
                    .email("doctor@medixtractor.local")
                    .password(hashedPassword)
                    .fullName("Dr. Jean Dupont")
                    .role("DOCTOR")
                    .siretSiren("123456789")  // 9 digits SIREN for doctor
                    .token(token)
                    .build();

                userRepository.save(doctorUser);
                System.out.println("✓ Doctor user created - Email: doctor@medixtractor.local | Password: DoctorPassword123!");
            }
        };
    }
}
