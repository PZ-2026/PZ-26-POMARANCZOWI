package pl.pomaranczowi.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pomaranczowi.backend.dto.AvailabilityDto;
import pl.pomaranczowi.backend.service.AvailabilityService;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @GetMapping("/{barberId}")
    public ResponseEntity<List<AvailabilityDto>> getAvailabilityByBarber(@PathVariable Long barberId) {
        return ResponseEntity.ok(availabilityService.getAvailabilityByBarber(barberId));
    }

    @PostMapping
    public ResponseEntity<AvailabilityDto> createAvailability(
            @RequestBody AvailabilityDto dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(availabilityService.createAvailability(dto, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityDto> updateAvailability(
            @PathVariable Long id,
            @RequestBody AvailabilityDto dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(availabilityService.updateAvailability(id, dto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        availabilityService.deleteAvailability(id, userId);
        return ResponseEntity.ok().build();
    }
}