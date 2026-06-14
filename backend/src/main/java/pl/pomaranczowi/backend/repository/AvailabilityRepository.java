package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.Availability;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Availability} entity.
 * Provides lookup by barber and by barber + day of week.
 */
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    /**
     * Finds all availability slots for a specific barber.
     *
     * @param barberId the barber ID
     * @return list of availability slots
     */
    List<Availability> findByBarberBarberId(Long barberId);

    /**
     * Finds availability slots for a barber on a specific day of the week.
     *
     * @param barberId  the barber ID
     * @param dayOfWeek the day of week (1=Monday, 7=Sunday)
     * @return the availability slot, if found
     */
    Optional<Availability> findByBarberBarberIdAndDayOfWeek(Long barberId, Integer dayOfWeek);

    /**
     * Checks if an availability slot exists for a barber on a specific day of the week.
     *
     * @param barberId  the barber ID
     * @param dayOfWeek the day of week (1=Monday, 7=Sunday)
     * @return true if an availability slot exists
     */
    Boolean existsByBarberBarberIdAndDayOfWeek(Long barberId, Integer dayOfWeek);
}
