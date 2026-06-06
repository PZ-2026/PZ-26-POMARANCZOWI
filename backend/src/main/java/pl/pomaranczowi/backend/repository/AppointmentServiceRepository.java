package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.AppointmentService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the {@link AppointmentService} join entity.
 * Provides lookup by appointment, by date range, and by barber.
 */
public interface AppointmentServiceRepository
        extends JpaRepository<AppointmentService, Long> {

    /**
     * Finds all appointment-service records for a given appointment.
     *
     * @param appointmentId the appointment ID
     * @return list of associated appointment-service records
     */
    List<AppointmentService> findByAppointmentAppointmentId(Long appointmentId);

    /**
     * Finds all appointment-service records where the appointment's start time
     * is after the given date.
     *
     * @param after the lower bound date
     * @return list of matching appointment-service records
     */
    List<AppointmentService> findByAppointmentStartTimeAfter(LocalDateTime after);

    /**
     * Finds all appointment-service records for a given barber.
     *
     * @param barberId the barber ID
     * @return list of matching appointment-service records
     */
    List<AppointmentService> findByAppointmentBarberBarberId(Long barberId);
}
