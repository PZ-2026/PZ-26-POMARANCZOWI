package pl.pomaranczowi.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "barber_service")
public class BarberService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "barber_id")
    private Barber barber;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    public BarberService() {}

    public BarberService(Long id, Barber barber, Service service) {
        this.id = id;
        this.barber = barber;
        this.service = service;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Barber getBarber() { return barber; }
    public void setBarber(Barber barber) { this.barber = barber; }

    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
}