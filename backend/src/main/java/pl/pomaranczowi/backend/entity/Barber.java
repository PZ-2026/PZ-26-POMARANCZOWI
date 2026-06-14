package pl.pomaranczowi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * JPA entity linking a User to their barber profile.
 * Each barber has a specialization and an optional bio.
 */
@Entity
@Table(name = "barber")
public class Barber {

    /** Primary key, auto-generated identity column. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "barber_id")
    private Long barberId;

    /** System user linked to this barber profile. One-to-one relationship with {@link User}. */
    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** Barber's area of expertise (e.g. classic cut, beard styling). */
    private String specialization;
    /** Short biography or description of the barber. */
    private String bio;

    /**
     * Constructs a new {@link Barber} instance.
     */
    public Barber() {}

    /**
     * Constructs a new {@link Barber} instance with all fields.
     *
     * @param barberId       the unique identifier
     * @param user           the system user linked to this barber profile
     * @param specialization the barber's area of expertise
     * @param bio            a short biography
     */
    public Barber(Long barberId, User user, String specialization, String bio) {
        this.barberId = barberId;
        this.user = user;
        this.specialization = specialization;
        this.bio = bio;
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
     * Returns the {@link #user}.
     *
     * @return the {@link #user}
     */
    public User getUser() { return user; }

    /**
     * Sets the {@link #user}.
     *
     * @param user the {@link #user} to set
     */
    public void setUser(User user) { this.user = user; }

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
}
