package pl.pomaranczowi.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pomaranczowi.backend.dto.AuthResponse;
import pl.pomaranczowi.backend.dto.LoginRequest;
import pl.pomaranczowi.backend.dto.RegisterRequest;
import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.entity.UserRole;
import pl.pomaranczowi.backend.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPhone("123456789");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPhone("123456789");
        testUser.setPasswordHash("encodedPassword");
        testUser.setRole(UserRole.CLIENT);
    }

    @Test
    void register_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.register(registerRequest));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void login_InvalidEmail_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.login(loginRequest));

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.login(loginRequest));

        assertEquals("Invalid email or password", exception.getMessage());
    }
}