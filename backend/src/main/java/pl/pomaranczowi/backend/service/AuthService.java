package pl.pomaranczowi.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.pomaranczowi.backend.dto.AuthResponse;
import pl.pomaranczowi.backend.dto.LoginRequest;
import pl.pomaranczowi.backend.dto.RegisterRequest;
import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.entity.UserRole;
import pl.pomaranczowi.backend.repository.UserRepository;

import java.time.LocalDateTime;

/**
 * Service handling user authentication: registration, login, and current-user retrieval.
 * New users are always created with the CLIENT role.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * Registers a new user account and returns an authentication response with a JWT token.
     *
     * @param request the registration details (name, email, phone, password)
     * @return auth response containing JWT token and user profile data
     * @throws RuntimeException if the email is already registered
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(UserRole.CLIENT);

        user = userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }

    /**
     * Authenticates a user with email and password, returning a JWT token on success.
     *
     * @param request the login credentials (email, password)
     * @return auth response containing JWT token and user profile data
     * @throws RuntimeException if the email does not exist or the password is incorrect
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }

    /**
     * Retrieves the currently authenticated user's profile data from the security context.
     *
     * @return auth response with user profile data (without a new token)
     * @throws RuntimeException if the user is not authenticated or not found
     */
    public AuthResponse me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Unauthorized");
        }

        Long userId;
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            userId = (Long) principal;
        } else {
            userId = Long.parseLong(principal.toString());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(
                null,
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}
