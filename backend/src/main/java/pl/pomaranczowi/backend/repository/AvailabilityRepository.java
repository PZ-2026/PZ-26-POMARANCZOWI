package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
}