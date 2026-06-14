package pl.pomaranczowi.backend.dto;

import pl.pomaranczowi.backend.entity.UserRole;
import java.time.LocalDateTime;

/**
 * DTO for transferring user data.
 * The password field is used only for receiving data from the frontend during creation/update,
 * it should not be returned in responses.
 */
public class UserDto {

    private Long userId;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private UserRole role;
    private String password;

    public UserDto() {}

    public UserDto(Long userId, String name, String email, String phone, LocalDateTime createdAt, UserRole role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
        this.role = role;
    }

    // Getters and Setters

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}