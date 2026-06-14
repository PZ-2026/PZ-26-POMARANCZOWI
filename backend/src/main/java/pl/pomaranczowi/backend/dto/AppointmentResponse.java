package pl.pomaranczowi.backend.dto;

import pl.pomaranczowi.backend.entity.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for returning full appointment details with nested barber, client, and service data.
 */
public class AppointmentResponse {

    /** Unique identifier of the appointment. */
    private Long appointmentId;
    /** Barber assigned to the appointment. */
    private BarberDto barber;
    /** Client who booked the appointment. */
    private UserDto client;
    /** Scheduled start time of the appointment. */
    private LocalDateTime startTime;
    /** Scheduled end time of the appointment. */
    private LocalDateTime endTime;
    /** Current status of the appointment (e.g. BOOKED, CANCELLED, COMPLETED). */
    private AppointmentStatus status;
    /** Timestamp when the appointment was created. */
    private LocalDateTime createdAt;
    /** List of services included in the appointment. */
    private List<ServiceDto> services;

    /** Creates a new instance of {@link AppointmentResponse}. */
    public AppointmentResponse() {}

    /** Creates a new instance of {@link AppointmentResponse} with all fields.
     * @param appointmentId the {@link #appointmentId}
     * @param barber the {@link #barber}
     * @param client the {@link #client}
     * @param startTime the {@link #startTime}
     * @param endTime the {@link #endTime}
     * @param status the {@link #status}
     * @param createdAt the {@link #createdAt}
     * @param services the {@link #services} */
    public AppointmentResponse(Long appointmentId, BarberDto barber, UserDto client, LocalDateTime startTime,
                           LocalDateTime endTime, AppointmentStatus status, LocalDateTime createdAt, List<ServiceDto> services) {
        this.appointmentId = appointmentId;
        this.barber = barber;
        this.client = client;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
        this.services = services;
    }

    /** Returns the {@link #appointmentId}.
     * @return the {@link #appointmentId} */
    public Long getAppointmentId() { return appointmentId; }
    /** Sets the {@link #appointmentId}.
     * @param appointmentId the {@link #appointmentId} to set */
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    /** Returns the {@link #barber}.
     * @return the {@link #barber} */
    public BarberDto getBarber() { return barber; }
    /** Sets the {@link #barber}.
     * @param barber the {@link #barber} to set */
    public void setBarber(BarberDto barber) { this.barber = barber; }

    /** Returns the {@link #client}.
     * @return the {@link #client} */
    public UserDto getClient() { return client; }
    /** Sets the {@link #client}.
     * @param client the {@link #client} to set */
    public void setClient(UserDto client) { this.client = client; }

    /** Returns the {@link #startTime}.
     * @return the {@link #startTime} */
    public LocalDateTime getStartTime() { return startTime; }
    /** Sets the {@link #startTime}.
     * @param startTime the {@link #startTime} to set */
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    /** Returns the {@link #endTime}.
     * @return the {@link #endTime} */
    public LocalDateTime getEndTime() { return endTime; }
    /** Sets the {@link #endTime}.
     * @param endTime the {@link #endTime} to set */
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    /** Returns the {@link #status}.
     * @return the {@link #status} */
    public AppointmentStatus getStatus() { return status; }
    /** Sets the {@link #status}.
     * @param status the {@link #status} to set */
    public void setStatus(AppointmentStatus status) { this.status = status; }

    /** Returns the {@link #createdAt}.
     * @return the {@link #createdAt} */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /** Sets the {@link #createdAt}.
     * @param createdAt the {@link #createdAt} to set */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** Returns the {@link #services}.
     * @return the {@link #services} */
    public List<ServiceDto> getServices() { return services; }
    /** Sets the {@link #services}.
     * @param services the {@link #services} to set */
    public void setServices(List<ServiceDto> services) { this.services = services; }
}
