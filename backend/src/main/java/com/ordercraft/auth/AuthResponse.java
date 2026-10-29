package com.ordercraft.auth;

public class AuthResponse {
    private String token;
    private String fullName;
    private String email;
    private AccountRole role;

    public AuthResponse(String token, AppUser user) {
        this.token = token;
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public String getToken() { return token; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public AccountRole getRole() { return role; }
}
