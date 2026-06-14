package pl.pomaranczowi.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for user login with validation constraints.
 */
public class LoginRequest {

    /** Email address of the user attempting to log in. */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /** Password of the user attempting to log in. */
    @NotBlank(message = "Password is required")
    private String password;

    /** Creates a new instance of {@link LoginRequest}. */
    public LoginRequest() {}

    /**
     * Constructs a new {@link LoginRequest} with the specified values.
     *
     * @param email    the {@link #email}
     * @param password the {@link #password}
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

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
     * Returns the {@link #password}.
     *
     * @return the {@link #password}
     */
    public String getPassword() { return password; }
    /**
     * Sets the {@link #password}.
     *
     * @param password the {@link #password} to set
     */
    public void setPassword(String password) { this.password = password; }
}
