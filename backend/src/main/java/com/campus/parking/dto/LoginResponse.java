package com.campus.parking.dto;
public record LoginResponse(String token, String tokenType, long expiresIn, UserView user) {
    public record UserView(Long id, String fullName, String email, String role) {}
}
