package pl.pomaranczowi.backend.dto;

import pl.pomaranczowi.backend.entity.UserRole;

/**
 * DTO for transferring barber profile data along with embedded user fields.
 */
public class BarberDto {

    /** Unique identifier of the barber. */
    private Long barberId;
    /** Full name of the barber. */
    private String name;
    /** Email address of the barber. */
    private String email;
    /** Phone number of the barber. */
    private String phone;
    /** Specialization of the barber. */
    private String specialization;
    /** Short biography of the barber. */
    private String bio;
    /** Role assigned to the barber. */
    private UserRole role;

    /**
     * Creates a new instance of {@link BarberDto}.
     */
    public BarberDto() {}

    /**
     * Constructs a barber DTO with the given details.
     *
     * @param barberId the unique identifier of the barber
     * @param name the full name of the barber
     * @param email the email address of the barber
     * @param phone the phone number of the barber
     * @param specialization the specialization of the barber
     * @param bio the short biography of the barber
     * @param role the role assigned to the barber
     */
    public BarberDto(Long barberId, String name, String email, String phone, String specialization, String bio, UserRole role) {
        this.barberId = barberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.bio = bio;
        this.role = role;
    }

    /**
     * Returns the {@link #barberId}.
     *
     * @return the {@link #barberId}
     */
    public Long getBarberId() { return barberId; }
    /**
     * Sets the {@link #barberId}.
     *
     * @param barberId the {@link #barberId} to set
     */
    public void setBarberId(Long barberId) { this.barberId = barberId; }

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
     * Returns the {@link #specialization}.
     *
     * @return the {@link #specialization}
     */
    public String getSpecialization() { return specialization; }
    /**
     * Sets the {@link #specialization}.
     *
     * @param specialization the {@link #specialization} to set
     */
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    /**
     * Returns the {@link #bio}.
     *
     * @return the {@link #bio}
     */
    public String getBio() { return bio; }
    /**
     * Sets the {@link #bio}.
     *
     * @param bio the {@link #bio} to set
     */
    public void setBio(String bio) { this.bio = bio; }

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
