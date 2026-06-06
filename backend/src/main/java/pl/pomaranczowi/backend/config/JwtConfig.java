package pl.pomaranczowi.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Configuration for JWT token signing and validation.
 * Reads the secret key and expiration time from application properties.
 */
@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration}")
    private Long expirationMs;

    /**
     * Creates a {@link SecretKey} from the Base64-encoded secret string.
     * Pads the key to at least 32 bytes if necessary for HMAC-SHA algorithms.
     *
     * @return the HMAC secret key
     */
    @Bean
    public SecretKey secretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretString);
        if (keyBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            return Keys.hmacShaKeyFor(paddedKey);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Returns the token expiration time in milliseconds.
     *
     * @return expiration in milliseconds
     */
    public long getExpirationMs() {
        return expirationMs;
    }

    /**
     * Returns the Base64-encoded secret string used for signing tokens.
     *
     * @return the secret string
     */
    public String getSecretString() {
        return secretString;
    }
}
