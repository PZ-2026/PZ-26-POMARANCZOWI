package pl.pomaranczowi.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentRequest {

    @NotNull(message = "Barber ID is required")
    private Long barberId;

    @NotEmpty(message = "At least one service is required")
    private List<Long> serviceIds;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    public AppointmentRequest() {}

    public AppointmentRequest(Long barberId, List<Long> serviceIds, LocalDateTime startTime) {
        this.barberId = barberId;
        this.serviceIds = serviceIds;
        this.startTime = startTime;
    }

    public Long getBarberId() { return barberId; }
    public void setBarberId(Long barberId) { this.barberId = barberId; }

    public List<Long> getServiceIds() { return serviceIds; }
    public void setServiceIds(List<Long> serviceIds) { this.serviceIds = serviceIds; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
}