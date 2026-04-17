package AHA.medixtractor.dto;

public record SignupRequest(
    String fullName,
    String email,
    String password,
    String role,  // DOCTOR, PHARMACIST, or ADMIN
    String siretSiren  // Required for DOCTOR and PHARMACIST, null for ADMIN
) {}
