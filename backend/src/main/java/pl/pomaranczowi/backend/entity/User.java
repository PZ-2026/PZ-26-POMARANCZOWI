package pl.pomaranczowi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * JPA entity representing a system user (client, employee, or admin).
 * Each user has authentication credentials (password hash), contact details,
 * a role, and an optional linked barber profile.
 */
@Entity
@Table(name = "\"user\"")
public class User {

    /** Primary key, auto-generated identity column. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /** User's display name. */
    @NotBlank
    private String name;

    /** User's email address (used as login). */
    @Email
    @NotBlank
    private String email;

    /** User's contact phone number. */
    @NotBlank
    private String phone;

    /** Bcrypt hash of the user's password. */
    @NotBlank
    @Column(name = "password_hash")
    private String passwordHash;

    /** Timestamp when the user account was created. */
    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** User's system role ({@link UserRole}). */
    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /** Optional barber profile linked to this user. Inverse side of {@link Barber#user}. */
    @OneToOne(mappedBy = "user")
    private Barber barber;

    /**
     * Constructs a new {@link User} instance.
     */
    public User() {}

    /**
     * Constructs a new {@link User} instance with all fields (excluding the barber profile).
     *
     * @param userId       the unique identifier
     * @param name         the user's display name
     * @param email        the user's email address
     * @param phone        the user's phone number
     * @param passwordHash the bcrypt password hash
     * @param createdAt    the account creation timestamp
     * @param role         the user's system role
     */
    public User(Long userId, String name, String email, String phone,
                String passwordHash, LocalDateTime createdAt, UserRole role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.role = role;
    }

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
     * Returns the {@link #passwordHash}.
     *
     * @return the {@link #passwordHash}
     */
    public String getPasswordHash() { return passwordHash; }

    /**
     * Sets the {@link #passwordHash}.
     *
     * @param passwordHash the {@link #passwordHash} to set
     */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /**
     * Returns the {@link #createdAt}.
     *
     * @return the {@link #createdAt}
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Sets the {@link #createdAt}.
     *
     * @param createdAt the {@link #createdAt} to set
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

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

    /**
     * Returns the {@link #barber}.
     *
     * @return the {@link #barber}
     */
    public Barber getBarber() { return barber; }

    /**
     * Sets the {@link #barber}.
     *
     * @param barber the {@link #barber} to set
     */
    public void setBarber(Barber barber) { this.barber = barber; }
}
