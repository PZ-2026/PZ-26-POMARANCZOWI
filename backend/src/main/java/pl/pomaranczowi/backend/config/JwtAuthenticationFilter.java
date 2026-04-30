package pl.pomaranczowi.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.pomaranczowi.backend.config.JwtConfig;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String secretString = jwtConfig.getSecretString();
                SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretString));

                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                Long userId = Long.parseLong(claims.getSubject());
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);

                request.setAttribute("userId", userId);
                request.setAttribute("email", email);
                request.setAttribute("role", role);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userId, null, new ArrayList<>()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                // Invalid token - don't set attributes
            }
        }

        filterChain.doFilter(request, response);
    }
}