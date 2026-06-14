package pl.pomaranczowi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalTime;

/**
 * JPA entity storing a barber's weekly availability window (day of week + time range).
 */
@Entity
@Table(name = "availability")
public class Availability {

    /** Primary key, auto-generated identity column. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id")
    private Long availabilityId;

    /** Barber this availability slot belongs to. Many-to-one relationship with {@link Barber}. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "barber_id")
    private Barber barber;

    /** Day of the week (1=Monday, 7=Sunday). */
    @NotNull
    @Column(name = "day_of_week")
    private Integer dayOfWeek;

    /** Availability window start time. */
    @NotNull
    @Column(name = "start_time")
    private LocalTime startTime;

    /** Availability window end time. */
    @NotNull
    @Column(name = "end_time")
    private LocalTime endTime;

    /**
     * Constructs a new {@link Availability} instance.
     */
    public Availability() {}

    /**
     * Constructs a new {@link Availability} instance with all fields.
     *
     * @param availabilityId the unique identifier
     * @param barber         the barber this availability belongs to
     * @param dayOfWeek      the day of the week (1=Monday, 7=Sunday)
     * @param startTime      the start time of the availability window
     * @param endTime        the end time of the availability window
     */
    public Availability(Long availabilityId, Barber barber,
                        Integer dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.availabilityId = availabilityId;
        this.barber = barber;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the {@link #availabilityId}.
     *
     * @return the {@link #availabilityId}
     */
    public Long getAvailabilityId() { return availabilityId; }

    /**
     * Sets the {@link #availabilityId}.
     *
     * @param availabilityId the {@link #availabilityId} to set
     */
    public void setAvailabilityId(Long availabilityId) { this.availabilityId = availabilityId; }

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
     * Returns the {@link #dayOfWeek}.
     *
     * @return the {@link #dayOfWeek}
     */
    public Integer getDayOfWeek() { return dayOfWeek; }

    /**
     * Sets the {@link #dayOfWeek}.
     *
     * @param dayOfWeek the {@link #dayOfWeek} to set
     */
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    /**
     * Returns the {@link #startTime}.
     *
     * @return the {@link #startTime}
     */
    public LocalTime getStartTime() { return startTime; }

    /**
     * Sets the {@link #startTime}.
     *
     * @param startTime the {@link #startTime} to set
     */
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    /**
     * Returns the {@link #endTime}.
     *
     * @return the {@link #endTime}
     */
    public LocalTime getEndTime() { return endTime; }

    /**
     * Sets the {@link #endTime}.
     *
     * @param endTime the {@link #endTime} to set
     */
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
