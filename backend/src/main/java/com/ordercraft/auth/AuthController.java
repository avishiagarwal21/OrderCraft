package com.ordercraft.auth;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signUp(@Valid @RequestBody AuthRequest request) {
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Full name is required");
        }
        return authService.signUp(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) { return authService.login(request); }

    @GetMapping("/me")
    public AuthResponse currentUser(Authentication authentication) {
        AppUser user = authService.findByEmail(authentication.getName());
        return new AuthResponse(null, user);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            authService.logout(authorization.substring(7).trim());
        }
    }

    @GetMapping("/users")
    public List<UserSummary> listUsers() { return authService.listUsers(); }
}
