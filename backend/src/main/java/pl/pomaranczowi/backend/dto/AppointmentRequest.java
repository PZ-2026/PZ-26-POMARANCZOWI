package pl.pomaranczowi.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for creating or updating appointments with validation constraints.
 */
public class AppointmentRequest {

    /** Identifier of the barber for the appointment. */
    @NotNull(message = "Barber ID is required")
    private Long barberId;

    /** Identifiers of the requested services. */
    @NotEmpty(message = "At least one service is required")
    private List<Long> serviceIds;

    /** Desired start time of the appointment. */
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    /** Creates a new instance of {@link AppointmentRequest}. */
    public AppointmentRequest() {}

    /** Creates a new instance of {@link AppointmentRequest} with all fields.
     * @param barberId the {@link #barberId}
     * @param serviceIds the {@link #serviceIds}
     * @param startTime the {@link #startTime} */
    public AppointmentRequest(Long barberId, List<Long> serviceIds, LocalDateTime startTime) {
        this.barberId = barberId;
        this.serviceIds = serviceIds;
        this.startTime = startTime;
    }

    /** Returns the {@link #barberId}.
     * @return the {@link #barberId} */
    public Long getBarberId() { return barberId; }
    /** Sets the {@link #barberId}.
     * @param barberId the {@link #barberId} to set */
    public void setBarberId(Long barberId) { this.barberId = barberId; }

    /** Returns the {@link #serviceIds}.
     * @return the {@link #serviceIds} */
    public List<Long> getServiceIds() { return serviceIds; }
    /** Sets the {@link #serviceIds}.
     * @param serviceIds the {@link #serviceIds} to set */
    public void setServiceIds(List<Long> serviceIds) { this.serviceIds = serviceIds; }

    /** Returns the {@link #startTime}.
     * @return the {@link #startTime} */
    public LocalDateTime getStartTime() { return startTime; }
    /** Sets the {@link #startTime}.
     * @param startTime the {@link #startTime} to set */
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
}
