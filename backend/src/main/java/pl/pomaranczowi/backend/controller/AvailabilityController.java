package pl.pomaranczowi.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pomaranczowi.backend.dto.AvailabilityDto;
import pl.pomaranczowi.backend.service.AvailabilityService;

import java.util.List;
import java.time.LocalDate;
import java.time.Duration;
import java.time.format.DateTimeParseException;

/**
 * REST controller for managing barber availability windows
 * and querying available time slots for a specific date.
 */
@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    /**
     * GET /api/availability/{barberId} - Retrieves all availability windows for a barber.
     *
     * @param barberId the ID of the barber
     * @return list of availability DTOs
     */
    @GetMapping("/{barberId}")
    public ResponseEntity<List<AvailabilityDto>> getAvailabilityByBarber(@PathVariable Long barberId) {
        return ResponseEntity.ok(availabilityService.getAvailabilityByBarber(barberId));
    }

    /**
     * POST /api/availability - Creates a new availability window for the authenticated barber.
     *
     * @param dto    the availability data
     * @param userId the authenticated barber's user ID
     * @return the created availability DTO
     */
    @PostMapping
    public ResponseEntity<AvailabilityDto> createAvailability(
            @RequestBody AvailabilityDto dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(availabilityService.createAvailability(dto, userId));
    }

    /**
     * PUT /api/availability/{id} - Updates an existing availability window.
     *
     * @param id     the ID of the availability record
     * @param dto    the updated availability data
     * @param userId the authenticated barber's user ID
     * @return the updated availability DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityDto> updateAvailability(
            @PathVariable Long id,
            @RequestBody AvailabilityDto dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(availabilityService.updateAvailability(id, dto, userId));
    }

    /**
     * DELETE /api/availability/{id} - Deletes an availability window.
     *
     * @param id     the ID of the availability record
     * @param userId the authenticated barber's user ID
     * @return 200 OK on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        availabilityService.deleteAvailability(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/availability/barber/{barberId}/date/{date}/available-times - Returns available
     * time slots for a barber on a specific date. Slots are generated in 15-minute steps
     * within the barber's availability window, filtered by existing appointments.
     *
     * @param barberId        the ID of the barber
     * @param date            the date in ISO format (yyyy-MM-dd)
     * @param serviceDuration the duration of the service in ISO-8601 format (e.g. PT30M)
     * @return list of available time slot strings (format: "HH:mm")
     */
    @GetMapping("/barber/{barberId}/date/{date}/available-times")
    public ResponseEntity<List<String>> getAvailableTimes(
            @PathVariable Long barberId,
            @PathVariable String date,
            @RequestParam(name = "serviceDuration", defaultValue = "PT30M") Duration serviceDuration) {
        try {
            LocalDate ld = LocalDate.parse(date);
            return ResponseEntity.ok(availabilityService.getAvailableTimes(barberId, ld, serviceDuration));
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
