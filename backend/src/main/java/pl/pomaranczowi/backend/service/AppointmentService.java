package pl.pomaranczowi.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pomaranczowi.backend.dto.*;
import pl.pomaranczowi.backend.entity.*;
import pl.pomaranczowi.backend.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core service managing the full lifecycle of appointments.
 * Handles creation (with collision detection, 30-min advance rule,
 * barber availability checks), updates, cancellation, and status transitions.
 */
@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AppointmentServiceRepository appointmentServiceRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    /**
     * Retrieves all appointments for a given user (client).
     *
     * @param userId the ID of the client user
     * @return list of appointment responses for that user
     */
    public List<AppointmentResponse> getAppointmentsByUser(Long userId) {
        List<Appointment> appointments = appointmentRepository.findByClientUserId(userId);
        return appointments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves past appointments and appointments with CANCELLED or COMPLETED status for a user.
     *
     * @param userId the ID of the client user
     * @return list of historical appointment responses
     */
    public List<AppointmentResponse> getAppointmentHistory(Long userId) {
        List<Appointment> appointments = appointmentRepository.findHistoryByClientUserIdBeforeOrStatusIn(
            userId,
            LocalDateTime.now(),
            List.of(AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED)
        );
        return appointments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves upcoming (future) BOOKED appointments for a user.
     *
     * @param userId the ID of the client user
     * @return list of upcoming appointment responses
     */
    public List<AppointmentResponse> getUpcomingAppointments(Long userId) {
        List<Appointment> appointments = appointmentRepository
                .findByClientUserIdAndStartTimeAfterAndStatus(
                        userId, LocalDateTime.now(), AppointmentStatus.BOOKED);
        return appointments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves non-cancelled appointments for a barber on a given date (busy times).
     *
     * @param barberId the ID of the barber
     * @param date     the date to check (time portion is ignored)
     * @return list of busy appointment responses for that day
     */
    public List<AppointmentResponse> getBusyTimes(Long barberId, LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
        List<Appointment> appointments = appointmentRepository.findByBarberBarberIdAndStartTimeBetween(barberId, startOfDay, endOfDay);
        return appointments.stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all appointments assigned to a barber (identified by their user ID).
     *
     * @param barberUserId the user ID of the barber
     * @return list of appointment responses for that barber
     */
    public List<AppointmentResponse> getAppointmentsForBarber(Long barberUserId) {
        List<Appointment> appointments = appointmentRepository.findByBarberUserUserId(barberUserId);
        return appointments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates the status of an appointment. Only the barber assigned to the appointment
     * is authorized to change its status.
     *
     * @param appointmentId the ID of the appointment to update
     * @param status        the new status (e.g. BOOKED, COMPLETED, CANCELLED)
     * @param barberUserId  the user ID of the barber requesting the change
     * @return the updated appointment response
     * @throws RuntimeException if the appointment is not found or the barber is not authorized
     */
    public AppointmentResponse updateStatus(Long appointmentId, AppointmentStatus status, Long barberUserId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getBarber().getUser().getUserId().equals(barberUserId)) {
            throw new RuntimeException("Not authorized");
        }

        appointment.setStatus(status);
        appointment = appointmentRepository.save(appointment);
        return mapToResponse(appointment);
    }

    /**
     * Creates a new appointment with validation:
     * <ul>
     *   <li>Must be booked at least 30 minutes in advance</li>
     *   <li>Barber must exist and work on the requested day</li>
     *   <li>All requested services must exist</li>
     *   <li>Time slot must not collide with existing non-cancelled appointments</li>
     * </ul>
     *
     * @param request the appointment request containing barber ID, service IDs, and start time
     * @param userId  the ID of the client creating the appointment
     * @return the created appointment response with computed end time
     * @throws RuntimeException if any validation rule is violated
     */
    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request, Long userId) {
        LocalDateTime minTime = LocalDateTime.now().plusMinutes(30);
        if (request.getStartTime().isBefore(minTime)) {
            throw new RuntimeException("Booking available minimum 30 minutes in advance");
        }

        User client = new User();
        client.setUserId(userId);

        Barber barber = barberRepository.findById(request.getBarberId())
                .orElseThrow(() -> new RuntimeException("Barber not found"));

        int totalDuration = 0;
        List<pl.pomaranczowi.backend.entity.Service> services = new ArrayList<>();
        for (Long serviceId : request.getServiceIds()) {
            pl.pomaranczowi.backend.entity.Service service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Service not found: " + serviceId));
            totalDuration += service.getDurationMinutes();
            services.add(service);
        }

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(totalDuration);

        int dayOfWeek = startTime.getDayOfWeek().getValue();
        boolean barberWorks = availabilityRepository.findByBarberBarberId(barber.getBarberId())
                .stream()
                .anyMatch(a -> a.getDayOfWeek() == dayOfWeek);
        if (!barberWorks) {
            throw new RuntimeException("Barber does not work on this day");
        }

        List<Appointment> existingAppointments = appointmentRepository.findByBarberBarberIdAndStartTimeAfter(
                barber.getBarberId(), startTime.minusHours(24));
        boolean collision = existingAppointments.stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .anyMatch(a -> !(a.getEndTime().isBefore(startTime) || a.getStartTime().isAfter(endTime)));
        if (collision) {
            throw new RuntimeException("Time slot is occupied");
        }

        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setBarber(barber);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setStatus(AppointmentStatus.BOOKED);

        appointment = appointmentRepository.save(appointment);

        for (pl.pomaranczowi.backend.entity.Service service : services) {
            pl.pomaranczowi.backend.entity.AppointmentService as = new pl.pomaranczowi.backend.entity.AppointmentService();
            as.setAppointment(appointment);
            as.setService(service);
            appointmentServiceRepository.save(as);
        }

        return mapToResponse(appointment);
    }

    /**
     * Updates an existing appointment's start time and/or services.
     * Only the client who owns the appointment is authorized to edit it.
     *
     * @param id      the ID of the appointment to update
     * @param request the updated appointment request data
     * @param userId  the ID of the client requesting the update
     * @return the updated appointment response
     * @throws RuntimeException if the appointment is not found or the user is not authorized
     */
    @Transactional
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request, Long userId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getClient().getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to edit this appointment");
        }

        if (request.getStartTime() != null && request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
            int totalDuration = 0;
            for (Long serviceId : request.getServiceIds()) {
                pl.pomaranczowi.backend.entity.Service service = serviceRepository.findById(serviceId)
                        .orElseThrow(() -> new RuntimeException("Service not found"));
                totalDuration += service.getDurationMinutes();
            }
            appointment.setStartTime(request.getStartTime());
            appointment.setEndTime(request.getStartTime().plusMinutes(totalDuration));
        }

        appointment = appointmentRepository.save(appointment);
        return mapToResponse(appointment);
    }

    /**
     * Cancels an appointment by setting its status to CANCELLED.
     * Only the client who owns the appointment is authorized to cancel it.
     *
     * @param id     the ID of the appointment to cancel
     * @param userId the ID of the client requesting cancellation
     * @throws RuntimeException if the appointment is not found or the user is not authorized
     */
    @Transactional
    public void cancelAppointment(Long id, Long userId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getClient().getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to cancel this appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    /**
     * Maps an Appointment entity to its full response DTO, including nested barber,
     * client, and service data.
     *
     * @param appointment the appointment entity
     * @return the fully populated appointment response DTO
     */
    private AppointmentResponse mapToResponse(Appointment appointment) {
        BarberDto barberDto = new BarberDto(
                appointment.getBarber().getBarberId(),
                appointment.getBarber().getUser().getName(),
                appointment.getBarber().getUser().getEmail(),
                appointment.getBarber().getUser().getPhone(),
                appointment.getBarber().getSpecialization(),
                appointment.getBarber().getBio(),
                appointment.getBarber().getUser().getRole()
        );

            UserDto clientDto = new UserDto(
                appointment.getClient().getUserId(),
                appointment.getClient().getName(),
                appointment.getClient().getEmail(),
                appointment.getClient().getPhone(),
                appointment.getClient().getCreatedAt(),
                appointment.getClient().getRole()
            );

            List<ServiceDto> serviceDtos = appointmentServiceRepository
                .findByAppointmentAppointmentId(appointment.getAppointmentId())
                .stream()
                .map(appointmentService -> {
                    pl.pomaranczowi.backend.entity.Service service = appointmentService.getService();
                    return new ServiceDto(
                        service.getServiceId(),
                        service.getName(),
                        service.getDescription(),
                        service.getDurationMinutes(),
                        service.getPrice(),
                        service.getIsActive()
                    );
                })
                .collect(Collectors.toList());

        return new AppointmentResponse(
                appointment.getAppointmentId(),
                barberDto,
                clientDto,
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                appointment.getCreatedAt(),
                serviceDtos
        );
    }
}
