package pl.pomaranczowi.backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pomaranczowi.backend.dto.AppointmentRequest;
import pl.pomaranczowi.backend.dto.AppointmentResponse;
import pl.pomaranczowi.backend.entity.AppointmentStatus;
import pl.pomaranczowi.backend.service.AppointmentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByUser(userId));
    }

    @GetMapping("/barber/{barberId}/busy-times")
    public ResponseEntity<List<AppointmentResponse>> getBusyTimes(
            @PathVariable Long barberId,
            @RequestParam String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date + "T00:00:00");
        return ResponseEntity.ok(appointmentService.getBusyTimes(barberId, dateTime));
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(appointmentService.createAppointment(request, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        appointmentService.cancelAppointment(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/barber")
    public ResponseEntity<List<AppointmentResponse>> getBarberAppointments(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsForBarber(userId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status, userId));
    }
}