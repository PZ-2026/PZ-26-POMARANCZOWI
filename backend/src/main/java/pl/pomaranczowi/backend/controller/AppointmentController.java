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

/**
 * REST controller for managing the full appointment lifecycle.
 * Provides endpoints for CRUD operations, status changes, history,
 * and querying busy times for barbers.
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * GET /api/appointments - Retrieves all appointments for the authenticated user.
     *
     * @param userId the authenticated user's ID (from request attribute)
     * @return list of appointment responses
     */
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByUser(userId));
    }

    /**
     * GET /api/appointments/upcoming - Retrieves upcoming BOOKED appointments for the authenticated user.
     *
     * @param userId the authenticated user's ID (from request attribute)
     * @return list of upcoming appointment responses
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingAppointments(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(appointmentService.getUpcomingAppointments(userId));
    }

    /**
     * GET /api/appointments/history - Retrieves past and completed/cancelled appointments
     * for the authenticated user.
     *
     * @param userId the authenticated user's ID (from request attribute)
     * @return list of historical appointment responses
     */
    @GetMapping("/history")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentHistory(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(appointmentService.getAppointmentHistory(userId));
    }

    /**
     * GET /api/appointments/barber/{barberId}/busy-times - Retrieves busy (non-cancelled)
     * appointments for a barber on a specific date.
     *
     * @param barberId the ID of the barber
     * @param date     the date to query (format: yyyy-MM-dd)
     * @return list of busy appointment responses for that day
     */
    @GetMapping("/barber/{barberId}/busy-times")
    public ResponseEntity<List<AppointmentResponse>> getBusyTimes(
            @PathVariable Long barberId,
            @RequestParam String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date + "T00:00:00");
        return ResponseEntity.ok(appointmentService.getBusyTimes(barberId, dateTime));
    }

    /**
     * POST /api/appointments - Creates a new appointment for the authenticated user.
     *
     * @param request the appointment request (barberId, serviceIds, startTime)
     * @param userId  the authenticated user's ID (from request attribute)
     * @return the created appointment response
     */
    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(appointmentService.createAppointment(request, userId));
    }

    /**
     * PUT /api/appointments/{id} - Updates an existing appointment's start time and/or services.
     *
     * @param id      the ID of the appointment to update
     * @param request the updated appointment data
     * @param userId  the authenticated user's ID (from request attribute)
     * @return the updated appointment response
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request, userId));
    }

    /**
     * DELETE /api/appointments/{id} - Cancels an appointment by ID.
     *
     * @param id     the ID of the appointment to cancel
     * @param userId the authenticated user's ID (from request attribute)
     * @return 200 OK on successful cancellation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        appointmentService.cancelAppointment(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/appointments/barber - Retrieves all appointments for the barber associated
     * with the authenticated user.
     *
     * @param userId the authenticated barber's user ID (from request attribute)
     * @return list of appointment responses for that barber
     */
    @GetMapping("/barber")
    public ResponseEntity<List<AppointmentResponse>> getBarberAppointments(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsForBarber(userId));
    }

    /**
     * PUT /api/appointments/{id}/status - Updates the status of an appointment (barber only).
     *
     * @param id     the ID of the appointment
     * @param status the new status (BOOKED, COMPLETED, CANCELLED)
     * @param userId the authenticated barber's user ID (from request attribute)
     * @return the updated appointment response
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status, userId));
    }
}
