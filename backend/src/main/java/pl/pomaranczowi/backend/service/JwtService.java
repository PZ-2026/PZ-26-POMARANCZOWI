package pl.pomaranczowi.backend.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pomaranczowi.backend.config.JwtConfig;
import pl.pomaranczowi.backend.entity.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

/**
 * Service for generating and validating JWT tokens.
 * Tokens contain claims for userId (subject), email, name, and role.
 */
@Service
public class JwtService {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * Generates a JWT token for the given user.
     * The token includes the user ID as the subject and custom claims
     * for email, name, and role. Expiration is read from configuration.
     *
     * @param user the user entity to generate a token for
     * @return a signed JWT string
     */
    public String generateToken(User user) {
        String secretString = jwtConfig.getSecretString();
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretString));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationMs());

        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("role", user.getRole().toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the user ID from a JWT token.
     *
     * @param token the JWT string
     * @return the user ID stored as the token subject
     */
    public Long getUserIdFromToken(String token) {
        String secretString = jwtConfig.getSecretString();
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretString));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extracts the email claim from a JWT token.
     *
     * @param token the JWT string
     * @return the email address stored in the token
     */
    public String getEmailFromToken(String token) {
        String secretString = jwtConfig.getSecretString();
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretString));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("email", String.class);
    }

    /**
     * Extracts the role claim from a JWT token.
     *
     * @param token the JWT string
     * @return the user role stored in the token
     */
    public String getRoleFromToken(String token) {
        String secretString = jwtConfig.getSecretString();
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretString));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }

    /**
     * Validates whether a JWT token is correctly signed and not expired.
     *
     * @param token the JWT string to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            String secretString = jwtConfig.getSecretString();
            SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretString));

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
