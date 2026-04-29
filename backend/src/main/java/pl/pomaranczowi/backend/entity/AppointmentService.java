package pl.pomaranczowi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "appointment_service")
public class AppointmentService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    public AppointmentService() {}

    public AppointmentService(Long id, Appointment appointment, Service service) {
        this.id = id;
        this.appointment = appointment;
        this.service = service;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
}