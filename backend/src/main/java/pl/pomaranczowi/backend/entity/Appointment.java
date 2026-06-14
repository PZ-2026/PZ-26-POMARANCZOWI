package pl.pomaranczowi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * JPA entity representing a booked appointment with client, barber,
 * time window, and status.
 */
@Entity
@Table(name = "appointment")
public class Appointment {

    /** Primary key, auto-generated identity column. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    /** Client who booked the appointment. Many-to-one relationship with {@link User}. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    /** Barber assigned to the appointment. Many-to-one relationship with {@link Barber}. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "barber_id")
    private Barber barber;

    /** Appointment start date and time. */
    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /** Appointment end date and time. */
    @NotNull
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /** Timestamp when the appointment was created. */
    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Current status of the appointment ({@link AppointmentStatus}). */
    @NotNull
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    /**
     * Constructs a new {@link Appointment} instance.
     */
    public Appointment() {}

    /**
     * Constructs a new {@link Appointment} instance with all fields.
     *
     * @param appointmentId the unique identifier
     * @param client        the client who booked the appointment
     * @param barber        the barber assigned to the appointment
     * @param startTime     the appointment start time
     * @param endTime       the appointment end time
     * @param createdAt     the creation timestamp
     * @param status        the appointment status
     */
    public Appointment(Long appointmentId, User client, Barber barber,
                       LocalDateTime startTime, LocalDateTime endTime,
                       LocalDateTime createdAt, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.client = client;
        this.barber = barber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.status = status;
    }

    /**
     * Returns the {@link #appointmentId}.
     *
     * @return the {@link #appointmentId}
     */
    public Long getAppointmentId() { return appointmentId; }

    /**
     * Sets the {@link #appointmentId}.
     *
     * @param appointmentId the {@link #appointmentId} to set
     */
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    /**
     * Returns the {@link #client}.
     *
     * @return the {@link #client}
     */
    public User getClient() { return client; }

    /**
     * Sets the {@link #client}.
     *
     * @param client the {@link #client} to set
     */
    public void setClient(User client) { this.client = client; }

    /**
     * Returns the {@link #barber}.
     *
     * @return the {@link #barber}
     */
    public Barber getBarber() { return barber; }

    /**
     * Sets the {@link #barber}.
     *
     * @param barber the {@link #barber} to set
     */
    public void setBarber(Barber barber) { this.barber = barber; }

    /**
     * Returns the {@link #startTime}.
     *
     * @return the {@link #startTime}
     */
    public LocalDateTime getStartTime() { return startTime; }

    /**
     * Sets the {@link #startTime}.
     *
     * @param startTime the {@link #startTime} to set
     */
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    /**
     * Returns the {@link #endTime}.
     *
     * @return the {@link #endTime}
     */
    public LocalDateTime getEndTime() { return endTime; }

    /**
     * Sets the {@link #endTime}.
     *
     * @param endTime the {@link #endTime} to set
     */
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    /**
     * Returns the {@link #createdAt}.
     *
     * @return the {@link #createdAt}
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Sets the {@link #createdAt}.
     *
     * @param createdAt the {@link #createdAt} to set
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Returns the {@link #status}.
     *
     * @return the {@link #status}
     */
    public AppointmentStatus getStatus() { return status; }

    /**
     * Sets the {@link #status}.
     *
     * @param status the {@link #status} to set
     */
    public void setStatus(AppointmentStatus status) { this.status = status; }
}
