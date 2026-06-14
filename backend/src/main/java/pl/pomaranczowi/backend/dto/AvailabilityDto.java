package pl.pomaranczowi.backend.dto;

import java.time.LocalTime;

/**
 * DTO for transferring barber availability window data.
 */
public class AvailabilityDto {

    private Long availabilityId;
    private Long barberId;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public AvailabilityDto() {}

    public AvailabilityDto(Long availabilityId, Long barberId, Integer dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.availabilityId = availabilityId;
        this.barberId = barberId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(Long availabilityId) { this.availabilityId = availabilityId; }

    public Long getBarberId() { return barberId; }
    public void setBarberId(Long barberId) { this.barberId = barberId; }

    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}