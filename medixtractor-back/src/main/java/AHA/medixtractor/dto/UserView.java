package AHA.medixtractor.dto;

import AHA.medixtractor.model.User;

public record UserView(
    Long id,
    String email,
    String fullName,
    String role
) {
    public static UserView fromEntity(User user) {
        return new UserView(user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }
}
