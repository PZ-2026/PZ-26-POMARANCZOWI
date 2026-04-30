package pl.pomaranczowi.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pomaranczowi.backend.dto.AvailabilityDto;
import pl.pomaranczowi.backend.entity.Availability;
import pl.pomaranczowi.backend.entity.Barber;
import pl.pomaranczowi.backend.repository.AvailabilityRepository;
import pl.pomaranczowi.backend.repository.BarberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private BarberRepository barberRepository;

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
}