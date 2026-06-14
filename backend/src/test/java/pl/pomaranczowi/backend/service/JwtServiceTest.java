package pl.pomaranczowi.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pomaranczowi.backend.config.JwtConfig;
import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.entity.UserRole;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String BASE64_SECRET = "dGhpc2lzYXNlY3JldGtleWZvcmJhYmJlc2hvcHBhcHBwYXNz";
    private static final long EXPIRATION_MS = 86400000L;

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private JwtService jwtService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setRole(UserRole.CLIENT);

        when(jwtConfig.getSecretString()).thenReturn(BASE64_SECRET);
        lenient().when(jwtConfig.getExpirationMs()).thenReturn(EXPIRATION_MS);
    }

    @Test
    void generateToken_Success() {
        String token = jwtService.generateToken(testUser);

        assertAll("generated token",
            () -> assertNotNull(token),
            () -> assertFalse(token.isBlank()),
            () -> assertTrue(token.split("\\.").length == 3)
        );
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtService.generateToken(testUser);

        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        assertFalse(jwtService.validateToken("invalid.token.here"));
    }

    @Test
    void validateToken_GarbageString_ReturnsFalse() {
        assertFalse(jwtService.validateToken("not-a-jwt-token"));
    }

    @Test
    void getUserIdFromToken_ReturnsCorrectId() {
        String token = jwtService.generateToken(testUser);

        Long userId = jwtService.getUserIdFromToken(token);

        assertEquals(1L, userId);
    }

    @Test
    void getEmailFromToken_ReturnsCorrectEmail() {
        String token = jwtService.generateToken(testUser);

        String email = jwtService.getEmailFromToken(token);

        assertEquals("test@example.com", email);
    }

    @Test
    void getRoleFromToken_ReturnsCorrectRole() {
        String token = jwtService.generateToken(testUser);

        String role = jwtService.getRoleFromToken(token);

        assertEquals("CLIENT", role);
    }

    @Test
    void generateToken_DifferentUsers_ProducesDifferentTokens() {
        User anotherUser = new User();
        anotherUser.setUserId(2L);
        anotherUser.setEmail("other@example.com");
        anotherUser.setName("Other");
        anotherUser.setRole(UserRole.EMPLOYEE);

        String token1 = jwtService.generateToken(testUser);
        String token2 = jwtService.generateToken(anotherUser);

        assertNotEquals(token1, token2);
    }
}
