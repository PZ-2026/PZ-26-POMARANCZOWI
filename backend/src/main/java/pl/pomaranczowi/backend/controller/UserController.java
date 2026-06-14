package pl.pomaranczowi.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pomaranczowi.backend.dto.UserDto;
import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.entity.UserRole;
import pl.pomaranczowi.backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing users. Provides endpoints for CRUD operations on user accounts.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves a list of all users.
     *
     * @return list of all users
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(users);
    }

    /**
     * Creates a new user.
     *
     * @param userDto the user data
     * @return the created user
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setRole(userDto.getRole());
        user.setPasswordHash("default_password"); 
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(mapToDto(savedUser));
    }

    /**
     * Updates an existing user.
     *
     * @param id      the user ID
     * @param userDto the updated user data
     * @return the updated user, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userRepository.findById(id).map(user -> {
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setPhone(userDto.getPhone());
            user.setRole(userDto.getRole());
            
            User updatedUser = userRepository.save(user);
            
            return ResponseEntity.ok(mapToDto(updatedUser));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the user ID
     * @return 200 OK if deleted, 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
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