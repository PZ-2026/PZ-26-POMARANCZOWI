package pl.pomaranczowi.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pomaranczowi.backend.dto.ServiceDto;
import pl.pomaranczowi.backend.service.ServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ServiceDto>> getPopularServices(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(serviceService.getPopularServices(limit));
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<ServiceDto> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }
}