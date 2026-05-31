package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.Availability;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByBarberBarberId(Long barberId);
    Optional<Availability> findByBarberBarberIdAndDayOfWeek(Long barberId, Integer dayOfWeek);
}