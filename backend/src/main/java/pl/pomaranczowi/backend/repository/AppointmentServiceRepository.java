package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.AppointmentService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, Long> {
}