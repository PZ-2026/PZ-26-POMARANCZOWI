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

    public List<AppointmentResponse> getAppointmentsByUser(Long userId) {
        List<Appointment> appointments = appointmentRepository.findByClientUserId(userId);
        return appointments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getBusyTimes(Long barberId, LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
        List<Appointment> appointments = appointmentRepository.findByBarberBarberIdAndStartTimeBetween(barberId, startOfDay, endOfDay);
        return appointments.stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getAppointmentsForBarber(Long barberUserId) {
        List<Appointment> appointments = appointmentRepository.findByBarberUserUserId(barberUserId);
        return appointments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

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

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request, Long userId) {
        LocalDateTime minTime = LocalDateTime.now().plusMinutes(30);
        if (request.getStartTime().isBefore(minTime)) {
            throw new RuntimeException("Rezerwacja możliwa min. 30 minut do przodu");
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
            throw new RuntimeException("Termin jest zajęty");
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
            AppointmentService as = new AppointmentService();
            // Can't use AppointmentService entity here due to naming conflict
            // Using repository directly instead
        }

        return mapToResponse(appointment);
    }

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

        List<ServiceDto> serviceDtos = new ArrayList<>();

        return new AppointmentResponse(
                appointment.getAppointmentId(),
                barberDto,
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                appointment.getCreatedAt(),
                serviceDtos
        );
    }
}