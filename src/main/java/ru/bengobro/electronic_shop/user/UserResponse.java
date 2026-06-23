package ru.bengobro.electronic_shop.user;

public record UserResponse(Long id, String name, String email, String phone) {

    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getPhone());
    }
}
