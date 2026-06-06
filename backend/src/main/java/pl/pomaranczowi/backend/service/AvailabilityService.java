package pl.pomaranczowi.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pomaranczowi.backend.dto.AvailabilityDto;
import pl.pomaranczowi.backend.entity.Availability;
import pl.pomaranczowi.backend.entity.Barber;
import pl.pomaranczowi.backend.repository.AvailabilityRepository;
import pl.pomaranczowi.backend.repository.BarberRepository;
import pl.pomaranczowi.backend.repository.AppointmentRepository;
import pl.pomaranczowi.backend.util.TimeSlotUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing barber availability schedules.
 * Provides CRUD operations for availability windows and computes
 * available time slots by filtering out booked appointments
 * with 5-minute buffer windows.
 */
@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    /**
     * Retrieves all availability windows for a given barber.
     *
     * @param barberId the ID of the barber
     * @return list of availability DTOs for that barber
     */
    public List<AvailabilityDto> getAvailabilityByBarber(Long barberId) {
        List<Availability> availabilities = availabilityRepository.findByBarberBarberId(barberId);
        return availabilities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Maps an Availability entity to its DTO representation.
     *
     * @param availability the availability entity
     * @return the corresponding DTO
     */
    private AvailabilityDto mapToDto(Availability availability) {
        return new AvailabilityDto(
                availability.getAvailabilityId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }

    /**
     * Creates a new availability window for a barber.
     * The authenticated user must have a barber profile associated with their account.
     *
     * @param dto          the availability data to create
     * @param barberUserId the user ID of the barber
     * @return the created availability DTO
     * @throws RuntimeException if the user does not have a barber profile
     */
    public AvailabilityDto createAvailability(AvailabilityDto dto, Long barberUserId) {
        Barber barber = barberRepository.findByUserUserId(barberUserId);
        if (barber == null) {
            throw new RuntimeException("You are not a barber");
        }
        Availability availability = new Availability();
        availability.setBarber(barber);
        availability.setDayOfWeek(dto.getDayOfWeek());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        availability = availabilityRepository.save(availability);
        return mapToDto(availability);
    }

    /**
     * Updates an existing availability window.
     * Only the barber who owns the availability record is authorized to update it.
     *
     * @param id           the ID of the availability record to update
     * @param dto          the updated availability data
     * @param barberUserId the user ID of the barber
     * @return the updated availability DTO
     * @throws RuntimeException if the record is not found or the barber is not authorized
     */
    public AvailabilityDto updateAvailability(Long id, AvailabilityDto dto, Long barberUserId) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
        if (!availability.getBarber().getUser().getUserId().equals(barberUserId)) {
            throw new RuntimeException("Not authorized");
        }
        availability.setDayOfWeek(dto.getDayOfWeek());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        availability = availabilityRepository.save(availability);
        return mapToDto(availability);
    }

    /**
     * Deletes an availability window.
     * Only the barber who owns the availability record is authorized to delete it.
     *
     * @param id           the ID of the availability record to delete
     * @param barberUserId the user ID of the barber
     * @throws RuntimeException if the record is not found or the barber is not authorized
     */
    public void deleteAvailability(Long id, Long barberUserId) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
        if (!availability.getBarber().getUser().getUserId().equals(barberUserId)) {
            throw new RuntimeException("Not authorized");
        }
        availabilityRepository.delete(availability);
    }

    /**
     * Computes available time slots for a barber on a given date for a specific service duration.
     * <p>
     * The algorithm:
     * <ul>
     *   <li>Generates candidate slots in 15-minute steps within the barber's availability window</li>
     *   <li>Excludes slots that would end after the availability window (including a 5-min prep buffer)</li>
     *   <li>Filters out slots that overlap with existing non-cancelled appointments
     *       (each appointment is padded with 5-minute buffers before and after)</li>
     * </ul>
     *
     * @param barberId         the ID of the barber
     * @param date             the date to check availability for
     * @param serviceDuration  the duration of the service (used to compute slot end times)
     * @return list of available time slot strings (format: "HH:mm")
     * @throws RuntimeException if no availability is defined for the barber on that day of the week
     */
    public List<String> getAvailableTimes(Long barberId, LocalDate date, Duration serviceDuration) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        Availability availability = availabilityRepository.findByBarberBarberIdAndDayOfWeek(barberId, dayOfWeek)
            .orElseThrow(() -> new RuntimeException("No availability for this day"));

        LocalDateTime startDateTime = LocalDateTime.of(date, availability.getStartTime());
        LocalDateTime endDateTime = LocalDateTime.of(date, availability.getEndTime());

        List<pl.pomaranczowi.backend.entity.Appointment> appointments = appointmentRepository
            .findByBarberBarberIdAndStartTimeBetween(barberId, startDateTime, endDateTime)
            .stream()
            .filter(a -> a.getStatus() != pl.pomaranczowi.backend.entity.AppointmentStatus.CANCELLED)
            .collect(Collectors.toList());

        int serviceDurationMinutes = Math.toIntExact(serviceDuration.toMinutes());
        Duration prepBuffer = Duration.ofMinutes(5);
        Duration occupiedDuration = serviceDuration.plus(prepBuffer);

        List<String> timeSlots = TimeSlotUtil.generateTimeSlots(availability.getStartTime(), availability.getEndTime(), 15);

        LocalTime lastAllowedStart = availability.getEndTime().minus(occupiedDuration);

        return timeSlots.stream()
            .filter(slot -> {
                LocalTime lt;
                try {
                lt = LocalTime.parse(slot);
                } catch (Exception ex) {
                return false;
                }
                if (lt.isAfter(lastAllowedStart)) {
                    return false;
                }
                    LocalDateTime slotStart = LocalDateTime.of(date, lt);
                    LocalDateTime slotEnd = slotStart.plusMinutes(serviceDurationMinutes);
                    LocalDateTime occupiedUntil = slotEnd.plus(prepBuffer);

                    return appointments.stream().noneMatch(a -> {
                        LocalDateTime busyStart = a.getStartTime().minusMinutes(5);
                        LocalDateTime busyEnd = a.getEndTime().plusMinutes(5);
                        return slotStart.isBefore(busyEnd) && occupiedUntil.isAfter(busyStart);
                    });
            })
            .collect(Collectors.toList());
        }
}
