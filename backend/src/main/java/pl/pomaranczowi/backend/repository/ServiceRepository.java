package pl.pomaranczowi.backend.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.pomaranczowi.backend.entity.Service;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    @Query("""
        SELECT s FROM Service s
        LEFT JOIN AppointmentService asv ON asv.service = s
        LEFT JOIN Appointment a ON a = asv.appointment AND a.status <> 'CANCELLED'
        GROUP BY s
        ORDER BY COUNT(asv) DESC
    """)
    List<Service> findTopPopularServices(Pageable pageable);
}