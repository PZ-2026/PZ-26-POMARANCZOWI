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

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<AvailabilityDto> getAvailabilityByBarber(Long barberId) {
        List<Availability> availabilities = availabilityRepository.findByBarberBarberId(barberId);
        return availabilities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AvailabilityDto mapToDto(Availability availability) {
        return new AvailabilityDto(
                availability.getAvailabilityId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }

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

    public void deleteAvailability(Long id, Long barberUserId) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
        if (!availability.getBarber().getUser().getUserId().equals(barberUserId)) {
            throw new RuntimeException("Not authorized");
        }
        availabilityRepository.delete(availability);
    }

        // Get available times for a specific date, filtering out already booked appointments
        public List<String> getAvailableTimes(Long barberId, LocalDate date, Duration serviceDuration) {
        int dayOfWeek = date.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
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

        // Candidate start times are always in 15-minute steps.
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
                // Exclude slots that do not leave enough time for the service plus prep window
                if (lt.isAfter(lastAllowedStart)) {
                    return false;
                }
                    LocalDateTime slotStart = LocalDateTime.of(date, lt);
                    LocalDateTime slotEnd = slotStart.plusMinutes(serviceDurationMinutes);
                    LocalDateTime occupiedUntil = slotEnd.plus(prepBuffer);

                    // Disallow slot if it overlaps an appointment's padded busy window.
                    return appointments.stream().noneMatch(a -> {
                        LocalDateTime busyStart = a.getStartTime().minusMinutes(5); // 5 min buffer before appointment
                        LocalDateTime busyEnd = a.getEndTime().plusMinutes(5); // 5 min buffer after appointment
                        return slotStart.isBefore(busyEnd) && occupiedUntil.isAfter(busyStart);
                    });
            })
            .collect(Collectors.toList());
        }
}