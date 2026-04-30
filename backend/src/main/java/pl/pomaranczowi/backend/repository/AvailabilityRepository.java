package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByBarberBarberId(Long barberId);
}