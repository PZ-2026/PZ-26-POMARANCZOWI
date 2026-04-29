package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarberRepository extends JpaRepository<Barber, Long> {
}