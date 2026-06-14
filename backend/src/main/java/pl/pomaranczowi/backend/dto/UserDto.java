package pl.pomaranczowi.backend.dto;

import pl.pomaranczowi.backend.entity.UserRole;
import java.time.LocalDateTime;

/**
 * DTO for transferring user data without the password hash.
 */
public class UserDto {

    /** Unique identifier of the user. */
    private Long userId;
    /** Full name of the user. */
    private String name;
    /** Email address of the user. */
    private String email;
    /** Phone number of the user. */
    private String phone;
    /** Timestamp when the user account was created. */
    private LocalDateTime createdAt;
    /** Role assigned to the user (e.g. ADMIN, EMPLOYEE, CLIENT). */
    private UserRole role;

    /** Creates a new instance of {@link UserDto}. */
    public UserDto() {}

    /** Creates a new instance of {@link UserDto} with all fields.
     * @param userId the {@link #userId}
     * @param name the {@link #name}
     * @param email the {@link #email}
     * @param phone the {@link #phone}
     * @param createdAt the {@link #createdAt}
     * @param role the {@link #role} */
    public UserDto(Long userId, String name, String email, String phone, LocalDateTime createdAt, UserRole role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
        this.role = role;
    }

    // Getters and Setters

    /** Returns the {@link #userId}.
     * @return the {@link #userId} */
    public Long getUserId() { return userId; }
    /** Sets the {@link #userId}.
     * @param userId the {@link #userId} to set */
    public void setUserId(Long userId) { this.userId = userId; }

    /** Returns the {@link #name}.
     * @return the {@link #name} */
    public String getName() { return name; }
    /** Sets the {@link #name}.
     * @param name the {@link #name} to set */
    public void setName(String name) { this.name = name; }

    /** Returns the {@link #email}.
     * @return the {@link #email} */
    public String getEmail() { return email; }
    /** Sets the {@link #email}.
     * @param email the {@link #email} to set */
    public void setEmail(String email) { this.email = email; }

    /** Returns the {@link #phone}.
     * @return the {@link #phone} */
    public String getPhone() { return phone; }
    /** Sets the {@link #phone}.
     * @param phone the {@link #phone} to set */
    public void setPhone(String phone) { this.phone = phone; }

    /** Returns the {@link #createdAt}.
     * @return the {@link #createdAt} */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /** Sets the {@link #createdAt}.
     * @param createdAt the {@link #createdAt} to set */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** Returns the {@link #role}.
     * @return the {@link #role} */
    public UserRole getRole() { return role; }
    /** Sets the {@link #role}.
     * @param role the {@link #role} to set */
    public void setRole(UserRole role) { this.role = role; }
}