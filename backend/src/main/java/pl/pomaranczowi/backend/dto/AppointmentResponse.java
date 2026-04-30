package pl.pomaranczowi.backend.dto;

import pl.pomaranczowi.backend.entity.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentResponse {

    private Long appointmentId;
    private BarberDto barber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
    private List<ServiceDto> services;

    public AppointmentResponse() {}

    public AppointmentResponse(Long appointmentId, BarberDto barber, LocalDateTime startTime, 
                           LocalDateTime endTime, AppointmentStatus status, LocalDateTime createdAt, List<ServiceDto> services) {
        this.appointmentId = appointmentId;
        this.barber = barber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
        this.services = services;
    }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public BarberDto getBarber() { return barber; }
    public void setBarber(BarberDto barber) { this.barber = barber; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ServiceDto> getServices() { return services; }
    public void setServices(List<ServiceDto> services) { this.services = services; }
}