package pl.pomaranczowi.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pomaranczowi.backend.dto.ServiceDto;
import pl.pomaranczowi.backend.service.ServiceService;

import java.util.List;

/**
 * REST controller for the service catalog.
 * Provides endpoints to list all services, get popular services, and get a service by ID.
 */
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    /**
     * GET /api/services - Retrieves all available services.
     *
     * @return list of service DTOs
     */
    @GetMapping
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    /**
     * GET /api/services/popular - Retrieves the most popular services.
     *
     * @param limit the maximum number of services to return (default: 3)
     * @return list of popular service DTOs
     */
    @GetMapping("/popular")
    public ResponseEntity<List<ServiceDto>> getPopularServices(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(serviceService.getPopularServices(limit));
    }

    /**
     * GET /api/services/{id} - Retrieves a single service by ID.
     *
     * @param id the service ID
     * @return the service DTO
     */
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<ServiceDto> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }


    @PostMapping
    public ResponseEntity<ServiceDto> createService(@RequestBody ServiceDto serviceDto) {
        // Zakładam, że masz metodę save lub create w ServiceService
        return ResponseEntity.ok(serviceService.createService(serviceDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDto> updateService(@PathVariable Long id, @RequestBody ServiceDto serviceDto) {
    return ResponseEntity.ok(serviceService.updateService(id, serviceDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.ok().build();
    }
}

