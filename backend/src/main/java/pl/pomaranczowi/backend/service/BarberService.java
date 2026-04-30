package pl.pomaranczowi.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pomaranczowi.backend.dto.BarberDto;
import pl.pomaranczowi.backend.entity.Barber;
import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.entity.UserRole;
import pl.pomaranczowi.backend.repository.BarberRepository;
import pl.pomaranczowi.backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarberService {

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private UserRepository userRepository;

    public List<BarberDto> getAllBarbers() {
        List<Barber> barbers = barberRepository.findAll();
        return barbers.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BarberDto getBarberById(Long id) {
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Barber not found"));
        return mapToDto(barber);
    }

    private BarberDto mapToDto(Barber barber) {
        User user = barber.getUser();
        return new BarberDto(
                barber.getBarberId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                barber.getSpecialization(),
                barber.getBio(),
                user.getRole()
        );
    }
}