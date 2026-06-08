package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.BarberService;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the {@link BarberService} join entity.
 */
public interface BarberServiceRepository extends JpaRepository<BarberService, Long> {
}
