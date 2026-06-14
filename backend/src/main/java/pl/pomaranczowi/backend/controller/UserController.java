package pl.pomaranczowi.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.pomaranczowi.backend.dto.UserDto;
import pl.pomaranczowi.backend.entity.Barber;
import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.entity.UserRole;
import pl.pomaranczowi.backend.repository.BarberRepository;
import pl.pomaranczowi.backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BarberRepository barberRepository;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setRole(userDto.getRole());
        
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        } else {
            user.setPasswordHash(passwordEncoder.encode("TempPass123!"));
        }
        
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == UserRole.EMPLOYEE || savedUser.getRole() == UserRole.ADMIN) {
            Barber newBarber = new Barber();
            newBarber.setUser(savedUser);
            barberRepository.save(newBarber);
        }

        return ResponseEntity.ok(mapToDto(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userRepository.findById(id).map(user -> {
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setPhone(userDto.getPhone());
            
            user.setRole(userDto.getRole());
            
            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
            }
            
            User updatedUser = userRepository.save(user);

            if (updatedUser.getRole() == UserRole.EMPLOYEE || updatedUser.getRole() == UserRole.ADMIN) {
                Optional<Barber> existingBarber = barberRepository.findAll().stream()
                        .filter(b -> b.getUser().getUserId().equals(updatedUser.getUserId()))
                        .findFirst();
                
                if (existingBarber.isEmpty()) {
                    Barber newBarber = new Barber();
                    newBarber.setUser(updatedUser);
                    barberRepository.save(newBarber);
                }
            }
            
            return ResponseEntity.ok(mapToDto(updatedUser));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            Optional<Barber> barber = barberRepository.findAll().stream()
                    .filter(b -> b.getUser().getUserId().equals(id))
                    .findFirst();
            barber.ifPresent(b -> barberRepository.delete(b));

            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt(),
                user.getRole()
        );
    }
}