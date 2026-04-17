package AHA.medixtractor.dto;

public record AuthResponse(
    boolean success,
    String message,
    String token,
    UserView user
) {}
