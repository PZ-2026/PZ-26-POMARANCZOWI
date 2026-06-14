package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Barber} entity.
 * Provides lookup by associated user ID.
 */
public interface BarberRepository extends JpaRepository<Barber, Long> {
    /**
     * Finds a barber by the associated user's ID.
     *
     * @param userId the user ID
     * @return the barber, or null if not found
     */
    Barber findByUserUserId(Long userId);
}
