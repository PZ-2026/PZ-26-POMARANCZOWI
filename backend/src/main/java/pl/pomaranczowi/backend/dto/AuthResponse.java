package pl.pomaranczowi.backend.dto;

import pl.pomaranczowi.backend.entity.UserRole;

/**
 * DTO for authentication responses including JWT token and user profile data.
 */
public class AuthResponse {

    /** JWT authentication token. */
    private String token;
    /** Type of the authentication token (e.g. Bearer). */
    private String tokenType;
    /** Unique identifier of the authenticated user. */
    private Long userId;
    /** Full name of the authenticated user. */
    private String name;
    /** Email address of the authenticated user. */
    private String email;
    /** Phone number of the authenticated user. */
    private String phone;
    /** Role of the authenticated user. */
    private UserRole role;

    /**
     * Creates a new instance of {@link AuthResponse}.
     */
    public AuthResponse() {}

    /**
     * Constructs an authentication response with the given token, user details, and role.
     *
     * @param token the JWT authentication token
     * @param userId the unique identifier of the authenticated user
     * @param name the full name of the authenticated user
     * @param email the email address of the authenticated user
     * @param phone the phone number of the authenticated user
     * @param role the role of the authenticated user
     */
    public AuthResponse(String token, Long userId, String name, String email, String phone, UserRole role) {
        this.token = token;
        this.tokenType = "Bearer";
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    /**
     * Returns the {@link #token}.
     *
     * @return the {@link #token}
     */
    public String getToken() { return token; }
    /**
     * Sets the {@link #token}.
     *
     * @param token the {@link #token} to set
     */
    public void setToken(String token) { this.token = token; }

    /**
     * Returns the {@link #tokenType}.
     *
     * @return the {@link #tokenType}
     */
    public String getTokenType() { return tokenType; }
    /**
     * Sets the {@link #tokenType}.
     *
     * @param tokenType the {@link #tokenType} to set
     */
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    /**
     * Returns the {@link #userId}.
     *
     * @return the {@link #userId}
     */
    public Long getUserId() { return userId; }
    /**
     * Sets the {@link #userId}.
     *
     * @param userId the {@link #userId} to set
     */
    public void setUserId(Long userId) { this.userId = userId; }

    /**
     * Returns the {@link #name}.
     *
     * @return the {@link #name}
     */
    public String getName() { return name; }
    /**
     * Sets the {@link #name}.
     *
     * @param name the {@link #name} to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the {@link #email}.
     *
     * @return the {@link #email}
     */
    public String getEmail() { return email; }
    /**
     * Sets the {@link #email}.
     *
     * @param email the {@link #email} to set
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Returns the {@link #phone}.
     *
     * @return the {@link #phone}
     */
    public String getPhone() { return phone; }
    /**
     * Sets the {@link #phone}.
     *
     * @param phone the {@link #phone} to set
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Returns the {@link #role}.
     *
     * @return the {@link #role}
     */
    public UserRole getRole() { return role; }
    /**
     * Sets the {@link #role}.
     *
     * @param role the {@link #role} to set
     */
    public void setRole(UserRole role) { this.role = role; }
}
