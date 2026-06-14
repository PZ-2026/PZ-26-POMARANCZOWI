package pl.pomaranczowi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * JPA join-table entity mapping services to an appointment (many-to-many bridge).
 * A single appointment can have multiple services.
 */
@Entity
@Table(name = "appointment_service")
public class AppointmentService {

    /** Primary key, auto-generated identity column. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Appointment associated with this bridge record. Many-to-one relationship with {@link Appointment}. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    /** Service associated with this bridge record. Many-to-one relationship with {@link Service}. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    /**
     * Constructs a new {@link AppointmentService} instance.
     */
    public AppointmentService() {}

    /**
     * Constructs a new {@link AppointmentService} instance with all fields.
     *
     * @param id          the unique identifier
     * @param appointment the associated appointment
     * @param service     the associated service
     */
    public AppointmentService(Long id, Appointment appointment, Service service) {
        this.id = id;
        this.appointment = appointment;
        this.service = service;
    }

    /**
     * Returns the {@link #id}.
     *
     * @return the {@link #id}
     */
    public Long getId() { return id; }

    /**
     * Sets the {@link #id}.
     *
     * @param id the {@link #id} to set
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Returns the {@link #appointment}.
     *
     * @return the {@link #appointment}
     */
    public Appointment getAppointment() { return appointment; }

    /**
     * Sets the {@link #appointment}.
     *
     * @param appointment the {@link #appointment} to set
     */
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    /**
     * Returns the {@link #service}.
     *
     * @return the {@link #service}
     */
    public Service getService() { return service; }

    /**
     * Sets the {@link #service}.
     *
     * @param service the {@link #service} to set
     */
    public void setService(Service service) { this.service = service; }
}
