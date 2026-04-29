package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.BarberService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarberServiceRepository extends JpaRepository<BarberService, Long> {
}