package pl.pomaranczowi.backend.dto;

import java.time.LocalTime;

/**
 * DTO for transferring barber availability window data.
 */
public class AvailabilityDto {

    /** Unique identifier of the availability record. */
    private Long availabilityId;
    /** Identifier of the barber this availability belongs to. */
    private Long barberId;
    /** Day of the week for this availability slot. */
    private Integer dayOfWeek;
    /** Start time of the availability slot. */
    private LocalTime startTime;
    /** End time of the availability slot. */
    private LocalTime endTime;

    /** Creates a new instance of {@link AvailabilityDto}. */
    public AvailabilityDto() {
    }

    /**
     * Creates a new instance of {@link AvailabilityDto} with all fields.
     * 
     * @param availabilityId the {@link #availabilityId}
     * @param barberId       the {@link #barberId}
     * @param dayOfWeek      the {@link #dayOfWeek}
     * @param startTime      the {@link #startTime}
     * @param endTime        the {@link #endTime}
     */
    public AvailabilityDto(Long availabilityId, Long barberId, Integer dayOfWeek, LocalTime startTime,
            LocalTime endTime) {
        this.availabilityId = availabilityId;
        this.barberId = barberId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the {@link #availabilityId}.
     * 
     * @return the {@link #availabilityId}
     */
    public Long getAvailabilityId() {
        return availabilityId;
    }

    /**
     * Sets the {@link #availabilityId}.
     * 
     * @param availabilityId the {@link #availabilityId} to set
     */
    public void setAvailabilityId(Long availabilityId) {
        this.availabilityId = availabilityId;
    }

    /**
     * Returns the {@link #barberId}.
     * 
     * @return the {@link #barberId}
     */
    public Long getBarberId() {
        return barberId;
    }

    /**
     * Sets the {@link #barberId}.
     * 
     * @param barberId the {@link #barberId} to set
     */
    public void setBarberId(Long barberId) {
        this.barberId = barberId;
    }

    /**
     * Returns the {@link #dayOfWeek}.
     * 
     * @return the {@link #dayOfWeek}
     */
    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Sets the {@link #dayOfWeek}.
     * 
     * @param dayOfWeek the {@link #dayOfWeek} to set
     */
    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Returns the {@link #startTime}.
     * 
     * @return the {@link #startTime}
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the {@link #startTime}.
     * 
     * @param startTime the {@link #startTime} to set
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the {@link #endTime}.
     * 
     * @return the {@link #endTime}
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the {@link #endTime}.
     * 
     * @param endTime the {@link #endTime} to set
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}