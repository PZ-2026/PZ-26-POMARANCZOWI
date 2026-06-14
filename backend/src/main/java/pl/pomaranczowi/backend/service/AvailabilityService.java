package pl.pomaranczowi.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pomaranczowi.backend.dto.AvailabilityDto;
import pl.pomaranczowi.backend.entity.Availability;
import pl.pomaranczowi.backend.entity.Barber;
import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.entity.UserRole;
import pl.pomaranczowi.backend.repository.AvailabilityRepository;
import pl.pomaranczowi.backend.repository.BarberRepository;
import pl.pomaranczowi.backend.repository.AppointmentRepository;
import pl.pomaranczowi.backend.repository.UserRepository;
import pl.pomaranczowi.backend.util.TimeSlotUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing barber availability schedules.
 */
@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all availability slots for a specific barber.
     *
     * @param barberId the ID of the barber
     * @return list of availability DTOs for the barber
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
     * @return the corresponding availability DTO
     */
    private AvailabilityDto mapToDto(Availability availability) {
        return new AvailabilityDto(
                availability.getAvailabilityId(),
                availability.getBarber().getBarberId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }

    /**
     * Checks whether the given user has the ADMIN role.
     *
     * @param user the user to check
     * @return true if the user has ADMIN role, false otherwise
     */
    private boolean isAdmin(User user) {
        return user.getRole() != null && user.getRole().name().equalsIgnoreCase("ADMIN");
    }

    /**
     * Creates a new availability slot for a barber.
     * If the requesting user is an admin, the barber ID must be explicitly provided in the DTO.
     * If the user is a barber, their own barber profile is used.
     *
     * @param dto          the availability data
     * @param barberUserId the user ID of the barber (or admin) creating the slot
     * @return the created availability DTO
     * @throws RuntimeException if the user is not found, the barber is not found,
     *                          or an admin does not provide a barber ID
     */
    public AvailabilityDto createAvailability(AvailabilityDto dto, Long barberUserId) {
        User user = userRepository.findById(barberUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Barber barber;
        
        if (isAdmin(user)) {
            // Logika dla Admina
            if (dto.getBarberId() == null) throw new RuntimeException("Barber ID required for admin");
            barber = barberRepository.findById(dto.getBarberId())
                    .orElseThrow(() -> new RuntimeException("Barber not found"));
        } else {
            // Logika dla Fryzjera
            barber = barberRepository.findByUserUserId(barberUserId);
            if (barber == null) throw new RuntimeException("You are not a barber");
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
     * Updates an existing availability slot.
     * Only the barber who owns the slot or an admin may update it.
     *
     * @param id           the ID of the availability slot to update
     * @param dto          the updated availability data
     * @param barberUserId the user ID of the requester
     * @return the updated availability DTO
     * @throws RuntimeException if the slot is not found or the user is not authorized
     */
    public AvailabilityDto updateAvailability(Long id, AvailabilityDto dto, Long barberUserId) {
        User user = userRepository.findById(barberUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
        
        boolean isOwner = availability.getBarber().getUser().getUserId().equals(barberUserId);

        if (!isAdmin(user) && !isOwner) {
            throw new RuntimeException("Not authorized");
        }

        availability.setDayOfWeek(dto.getDayOfWeek());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        availability = availabilityRepository.save(availability);
        return mapToDto(availability);
    }

    /**
     * Deletes an availability slot by its ID.
     * Only the barber who owns the slot or an admin may delete it.
     *
     * @param id           the ID of the availability slot to delete
     * @param barberUserId the user ID of the requester
     * @throws RuntimeException if the slot is not found or the user is not authorized
     */
    public void deleteAvailability(Long id, Long barberUserId) {
        User user = userRepository.findById(barberUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
        
        boolean isOwner = availability.getBarber().getUser().getUserId().equals(barberUserId);

        if (!isAdmin(user) && !isOwner) {
            throw new RuntimeException("Not authorized");
        }
        
        availabilityRepository.delete(availability);
    }

    /**
     * Computes available time slots for a barber on a given date.
     * Considers the barber's defined availability, existing non-cancelled appointments,
     * and a 5-minute buffer before/after each appointment for collision detection.
     *
     * @param barberId        the ID of the barber
     * @param date            the date to check
     * @param serviceDuration the duration of the service to be booked
     * @return list of available time slot strings (e.g. "10:00", "10:15")
     * @throws RuntimeException if no availability is defined for that day
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
                try { lt = LocalTime.parse(slot); } catch (Exception ex) { return false; }
                if (lt.isAfter(lastAllowedStart)) return false;
                
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