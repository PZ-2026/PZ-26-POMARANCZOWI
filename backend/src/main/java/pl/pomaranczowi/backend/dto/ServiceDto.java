package pl.pomaranczowi.backend.dto;

/**
 * DTO for transferring service offering data.
 */
public class ServiceDto {

    /** Unique identifier of the service. */
    private Long serviceId;
    /** Name of the service. */
    private String name;
    /** Description of the service. */
    private String description;
    /** Duration of the service in minutes. */
    private Integer durationMinutes;
    /** Price of the service. */
    private Double price;
    /** Whether the service is currently active. */
    private Boolean isActive;

    /** Creates a new instance of {@link ServiceDto}. */
    public ServiceDto() {}

    /**
     * Constructs a new {@link ServiceDto} with the specified values.
     *
     * @param serviceId       the {@link #serviceId}
     * @param name            the {@link #name}
     * @param description     the {@link #description}
     * @param durationMinutes the {@link #durationMinutes}
     * @param price           the {@link #price}
     * @param isActive        the {@link #isActive}
     */
    public ServiceDto(Long serviceId, String name, String description, Integer durationMinutes, Double price, Boolean isActive) {
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
