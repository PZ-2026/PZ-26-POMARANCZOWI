package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.Appointment;
import pl.pomaranczowi.backend.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for {@link Appointment} entity.
 * Provides queries for user history, barber schedules, barber statistics,
 * and busy-time lookups.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Finds all appointments for a specific client.
     *
     * @param userId the client user ID
     * @return list of appointments
     */
    List<Appointment> findByClientUserId(Long userId);

    /**
     * Finds appointments for a client that started before a given time and have
     * any of the specified statuses.
     *
     * @param userId   the client user ID
     * @param before   the upper bound time
     * @param statuses list of statuses to include
     * @return list of matching appointments
     */
    List<Appointment> findByClientUserIdAndStartTimeBeforeAndStatusIn(
        Long userId,
        LocalDateTime before,
        List<AppointmentStatus> statuses
    );

    /**
     * Retrieves appointment history for a client: past appointments or
     * appointments with CANCELLED/COMPLETED status.
     *
     * @param userId   the client user ID
     * @param now      the current date-time
     * @param statuses list of statuses to include (CANCELLED, COMPLETED)
     * @return list of matching appointments ordered by start time
     */
    @Query("""
    SELECT a
    FROM Appointment a
    WHERE a.client.userId = :userId
        AND (a.startTime < :now OR a.status IN :statuses)
    ORDER BY a.startTime
    """)
    List<Appointment> findHistoryByClientUserIdBeforeOrStatusIn(
        @Param("userId") Long userId,
        @Param("now") LocalDateTime now,
        @Param("statuses") List<AppointmentStatus> statuses
    );

    /**
     * Retrieves upcoming (future) BOOKED appointments for a client.
     *
     * @param userId the client user ID
     * @param after  the current date-time
     * @param status the appointment status (BOOKED)
     * @return list of upcoming appointments
     */
    List<Appointment> findByClientUserIdAndStartTimeAfterAndStatus(
            Long userId,
            LocalDateTime after,
            AppointmentStatus status
    );

    /**
     * Retrieves appointments for a barber within a specific time range.
     *
     * @param barberId the barber ID
     * @param start    range start
     * @param end      range end
     * @return list of appointments in the range
     */
    List<Appointment> findByBarberBarberIdAndStartTimeBetween(
            Long barberId,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Retrieves appointments for a barber starting after a given time.
     * Used for collision detection during booking.
     *
     * @param barberId the barber ID
     * @param after    the lower bound time
     * @return list of matching appointments
     */
    List<Appointment> findByBarberBarberIdAndStartTimeAfter(
            Long barberId,
            LocalDateTime after
    );

    /**
     * Retrieves all appointments for a barber identified by their user ID.
     *
     * @param userId the barber's user ID
     * @return list of appointments for that barber
     */
    List<Appointment> findByBarberUserUserId(Long userId);

    /**
     * Retrieves all appointments with eagerly fetched client and barber user data.
     *
     * @return list of appointments with full details
     */
    @Query("""
        SELECT a
        FROM Appointment a
        JOIN FETCH a.client
        JOIN FETCH a.barber b
        JOIN FETCH b.user
        ORDER BY a.startTime
    """)
    List<Appointment> getAppointmentsWithDetails();

    /**
     * Retrieves barber statistics: name and appointment count, ordered by count descending.
     *
     * @return list of Object[] where [0]=barber name (String), [1]=count (Long)
     */
    @Query("""
        SELECT b.user.name, COUNT(a)
        FROM Appointment a
        JOIN a.barber b
        GROUP BY b.user.name
        ORDER BY COUNT(a) DESC
    """)
    List<Object[]> getBarberStatistics();

    /**
     * Finds all appointments for a specific barber.
     *
     * @param barberId the barber ID
     * @return list of appointments
     */
    List<Appointment> findByBarberBarberId(Long barberId);
}
