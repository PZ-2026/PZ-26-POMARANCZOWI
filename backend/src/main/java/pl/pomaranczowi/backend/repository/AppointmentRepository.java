package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.Appointment;
import pl.pomaranczowi.backend.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientUserId(Long userId);

    List<Appointment> findByClientUserIdAndStartTimeBeforeAndStatusIn(
        Long userId,
        LocalDateTime before,
        List<AppointmentStatus> statuses
    );

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

    List<Appointment> findByClientUserIdAndStartTimeAfterAndStatus(
            Long userId,
            LocalDateTime after,
            AppointmentStatus status
    );

    List<Appointment> findByBarberBarberIdAndStartTimeBetween(
            Long barberId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Appointment> findByBarberBarberIdAndStartTimeAfter(
            Long barberId,
            LocalDateTime after
    );

    List<Appointment> findByBarberUserUserId(Long userId);



    @Query("""
        SELECT a
        FROM Appointment a
        JOIN FETCH a.client
        JOIN FETCH a.barber b
        JOIN FETCH b.user
        ORDER BY a.startTime
    """)
    List<Appointment> getAppointmentsWithDetails();



    @Query("""
        SELECT b.user.name, COUNT(a)
        FROM Appointment a
        JOIN a.barber b
        GROUP BY b.user.name
        ORDER BY COUNT(a) DESC
    """)
    List<Object[]> getBarberStatistics();
}