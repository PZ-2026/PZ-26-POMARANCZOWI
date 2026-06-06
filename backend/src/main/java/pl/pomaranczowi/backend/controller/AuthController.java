package pl.pomaranczowi.backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pomaranczowi.backend.dto.AuthResponse;
import pl.pomaranczowi.backend.dto.LoginRequest;
import pl.pomaranczowi.backend.dto.RegisterRequest;
import pl.pomaranczowi.backend.service.AuthService;

/**
 * REST controller for user authentication endpoints.
 * Handles registration, login, and current-user retrieval.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * POST /api/auth/register - Registers a new user account.
     *
     * @param request the registration details
     * @return auth response with JWT token and user profile
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/login - Authenticates a user and returns a JWT token.
     *
     * @param request the login credentials
     * @return auth response with JWT token and user profile
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/me - Retrieves the currently authenticated user's profile.
     *
     * @return auth response with user profile (no token)
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me() {
        AuthResponse response = authService.me();
        return ResponseEntity.ok(response);
    }
}
