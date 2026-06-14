package pl.pomaranczowi.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user registration with validation constraints.
 */
public class RegisterRequest {

    /** Full name of the new user. */
    @NotBlank(message = "Name is required")
    private String name;

    /** Email address of the new user. */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /** Phone number of the new user. */
    @NotBlank(message = "Phone is required")
    private String phone;

    /** Password for the new account. */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /** Creates a new instance of {@link RegisterRequest}. */
    public RegisterRequest() {}

    /**
     * Constructs a new {@link RegisterRequest} with the specified values.
     *
     * @param name     the {@link #name}
     * @param email    the {@link #email}
     * @param phone    the {@link #phone}
     * @param password the {@link #password}
     */
    public RegisterRequest(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

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
