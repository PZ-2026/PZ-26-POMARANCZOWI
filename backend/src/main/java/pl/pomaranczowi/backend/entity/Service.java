package pl.pomaranczowi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * JPA entity representing a service offering (e.g. haircut, beard trim).
 * Each service has a name, description, duration, price, and active status.
 */
@Entity
@Table(name = "service")
public class Service {

    /** Primary key, auto-generated identity column. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long serviceId;

    /** Service display name (e.g. "Haircut", "Beard Trim"). */
    @NotBlank
    private String name;

    /** Optional detailed description of the service. */
    private String description;

    /** Duration of the service in minutes. */
    @NotNull
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    /** Price of the service in the base currency. Must be positive. */
    @NotNull
    @Positive
    private Double price;

    /** Whether the service is currently available for booking. */
    @NotNull
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Constructs a new {@link Service} instance.
     */
    public Service() {}

    /**
     * Constructs a new {@link Service} instance with all fields.
     *
     * @param serviceId       the unique identifier
     * @param name            the service display name
     * @param description     an optional detailed description
     * @param durationMinutes the duration in minutes
     * @param price           the price in the base currency
     * @param isActive        whether the service is available for booking
     */
    public Service(Long serviceId, String name, String description,
                   Integer durationMinutes, Double price, Boolean isActive) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.isActive = isActive;
    }

    /**
     * Returns the {@link #serviceId}.
     *
     * @return the {@link #serviceId}
     */
    public Long getServiceId() { return serviceId; }

    /**
     * Sets the {@link #serviceId}.
     *
     * @param serviceId the {@link #serviceId} to set
     */
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

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
     * Returns the {@link #description}.
     *
     * @return the {@link #description}
     */
    public String getDescription() { return description; }

    /**
     * Sets the {@link #description}.
     *
     * @param description the {@link #description} to set
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Returns the {@link #durationMinutes}.
     *
     * @return the {@link #durationMinutes}
     */
    public Integer getDurationMinutes() { return durationMinutes; }

    /**
     * Sets the {@link #durationMinutes}.
     *
     * @param durationMinutes the {@link #durationMinutes} to set
     */
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    /**
     * Returns the {@link #price}.
     *
     * @return the {@link #price}
     */
    public Double getPrice() { return price; }

    /**
     * Sets the {@link #price}.
     *
     * @param price the {@link #price} to set
     */
    public void setPrice(Double price) { this.price = price; }

    /**
     * Returns the {@link #isActive}.
     *
     * @return the {@link #isActive}
     */
    public Boolean getIsActive() { return isActive; }

    /**
     * Sets the {@link #isActive}.
     *
     * @param isActive the {@link #isActive} to set
     */
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
