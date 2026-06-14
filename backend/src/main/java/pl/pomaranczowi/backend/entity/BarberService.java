package pl.pomaranczowi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * JPA join-table entity mapping which barbers offer which services (many-to-many bridge).
 */
@Entity
@Table(name = "barber_service")
public class BarberService {

    /** Primary key, auto-generated identity column. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Barber associated with this bridge record. Many-to-one relationship with {@link Barber}. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "barber_id")
    private Barber barber;

    /** Service associated with this bridge record. Many-to-one relationship with {@link Service}. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    /**
     * Constructs a new {@link BarberService} instance.
     */
    public BarberService() {}

    /**
     * Constructs a new {@link BarberService} instance with all fields.
     *
     * @param id      the unique identifier
     * @param barber  the associated barber
     * @param service the associated service
     */
    public BarberService(Long id, Barber barber, Service service) {
        this.id = id;
        this.barber = barber;
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
