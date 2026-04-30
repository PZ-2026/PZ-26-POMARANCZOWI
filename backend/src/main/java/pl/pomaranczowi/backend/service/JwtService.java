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

@Service
public class JwtService {

    @Autowired
    private JwtConfig jwtConfig;

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