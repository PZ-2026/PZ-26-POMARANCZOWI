package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByClientUserId(Long userId);
    List<Appointment> findByBarberBarberIdAndStartTimeBetween(Long barberId, java.time.LocalDateTime start, java.time.LocalDateTime end);
    List<Appointment> findByBarberBarberIdAndStartTimeAfter(Long barberId, java.time.LocalDateTime after);
    List<Appointment> findByBarberUserUserId(Long userId);
}