package com.ordercraft.auth;

public class UserSummary {
    private Long id;
    private String fullName;
    private String email;
    private AccountRole role;

    public UserSummary(AppUser user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public AccountRole getRole() { return role; }
}
