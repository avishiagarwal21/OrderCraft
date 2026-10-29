package com.ordercraft.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final AuthTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(AppUserRepository userRepository, AuthTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public synchronized AuthResponse signUp(AuthRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        AppUser user = new AppUser();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(userRepository.count() == 0 ? AccountRole.ADMIN : AccountRole.STAFF);
        userRepository.save(user);
        return issueToken(user);
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        String email = normalizeEmail(request.getEmail());
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect");
        }
        return issueToken(user);
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> findUserForToken(String rawToken) {
        return tokenRepository.findByTokenHash(hashToken(rawToken))
                .filter(token -> token.getExpiresAt().isAfter(Instant.now()))
                .map(AuthToken::getUser);
    }

    @Transactional
    public void logout(String rawToken) {
        if (rawToken != null && !rawToken.isBlank()) {
            tokenRepository.deleteByTokenHash(hashToken(rawToken));
        }
    }

    @Transactional(readOnly = true)
    public List<UserSummary> listUsers() {
        return userRepository.findAll().stream().map(UserSummary::new).toList();
    }

    @Transactional(readOnly = true)
    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    private AuthResponse issueToken(AppUser user) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        AuthToken token = new AuthToken();
        token.setTokenHash(hashToken(rawToken));
        token.setUser(user);
        token.setExpiresAt(Instant.now().plus(8, ChronoUnit.HOURS));
        tokenRepository.save(token);
        return new AuthResponse(rawToken, user);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
