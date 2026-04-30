package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.AppointmentService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, Long> {
    List<AppointmentService> findByAppointmentAppointmentId(Long appointmentId);
}