package pl.pomaranczowi.backend.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.pomaranczowi.backend.entity.Service;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Service} entity.
 * Provides a custom JPQL query to find the most popular services
 * based on non-cancelled appointment count.
 */
public interface ServiceRepository extends JpaRepository<Service, Long> {

    /**
     * Retrieves the top N most popular services, ordered by the number
     * of non-cancelled appointments they are associated with.
     *
     * @param pageable pagination information (use PageRequest.of(0, limit))
     * @return list of services ordered by popularity descending
     */
    @Query("""
        SELECT s FROM Service s
        LEFT JOIN AppointmentService asv ON asv.service = s
        LEFT JOIN Appointment a ON a = asv.appointment AND a.status <> 'CANCELLED'
        GROUP BY s
        ORDER BY COUNT(asv) DESC
    """)
    List<Service> findTopPopularServices(Pageable pageable);
}
