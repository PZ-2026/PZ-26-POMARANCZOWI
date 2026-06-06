package pl.pomaranczowi.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pomaranczowi.backend.dto.BarberDto;
import pl.pomaranczowi.backend.service.BarberService;

import java.util.List;

/**
 * REST controller for querying barber profiles.
 * Provides endpoints to list all barbers and get details of a specific barber.
 */
@RestController
@RequestMapping("/api/barbers")
public class BarberController {

    @Autowired
    private BarberService barberService;

    /**
     * GET /api/barbers - Retrieves all barbers.
     *
     * @return list of barber DTOs
     */
    @GetMapping
    public ResponseEntity<List<BarberDto>> getAllBarbers() {
        return ResponseEntity.ok(barberService.getAllBarbers());
    }

    /**
     * GET /api/barbers/{id} - Retrieves a single barber by ID.
     *
     * @param id the barber ID
     * @return the barber DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<BarberDto> getBarberById(@PathVariable Long id) {
        return ResponseEntity.ok(barberService.getBarberById(id));
    }
}
