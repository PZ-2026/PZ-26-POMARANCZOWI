package pl.pomaranczowi.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pomaranczowi.backend.dto.AppointmentRequest;
import pl.pomaranczowi.backend.dto.AppointmentResponse;
import pl.pomaranczowi.backend.dto.BarberDto;
import pl.pomaranczowi.backend.entity.*;
import pl.pomaranczowi.backend.repository.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private BarberRepository barberRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private AppointmentServiceRepository appointmentServiceRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private User testClient;
    private User testBarberUser;
    private Barber testBarber;
    private pl.pomaranczowi.backend.entity.Service testService;
    private Availability testAvailability;
    private Appointment testAppointment;

    @BeforeEach
    void setUp() {
        testClient = new User();
        testClient.setUserId(1L);
        testClient.setName("Client");
        testClient.setEmail("client@test.com");
        testClient.setPhone("123456789");

        testBarberUser = new User();
        testBarberUser.setUserId(2L);
        testBarberUser.setName("Barber");
        testBarberUser.setEmail("barber@test.com");
        testBarberUser.setRole(UserRole.EMPLOYEE);

        testBarber = new Barber();
        testBarber.setBarberId(1L);
        testBarber.setUser(testBarberUser);
        testBarber.setSpecialization("Fade");
        testBarber.setBio("Expert barber");

        testService = new pl.pomaranczowi.backend.entity.Service();
        testService.setServiceId(1L);
        testService.setName("Haircut");
        testService.setDurationMinutes(30);
        testService.setPrice(50.0);

        testAvailability = new Availability();
        testAvailability.setAvailabilityId(1L);
        testAvailability.setBarber(testBarber);
        testAvailability.setDayOfWeek(1);
        testAvailability.setStartTime(LocalTime.of(9, 0));
        testAvailability.setEndTime(LocalTime.of(17, 0));

        testAppointment = new Appointment();
        testAppointment.setAppointmentId(1L);
        testAppointment.setClient(testClient);
        testAppointment.setBarber(testBarber);
        testAppointment.setStartTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        testAppointment.setEndTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(30));
        testAppointment.setStatus(AppointmentStatus.BOOKED);
        testAppointment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createAppointment_Success() {
        AppointmentRequest request = new AppointmentRequest();
        request.setBarberId(1L);
        request.setServiceIds(Arrays.asList(1L));
        request.setStartTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0));

        when(barberRepository.findById(1L)).thenReturn(Optional.of(testBarber));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
        when(availabilityRepository.findByBarberBarberId(1L)).thenReturn(Arrays.asList(testAvailability));
        when(appointmentRepository.findByBarberBarberIdAndStartTimeAfter(any(), any())).thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment a = invocation.getArgument(0);
            a.setAppointmentId(1L);
            return a;
        });

        AppointmentResponse response = appointmentService.createAppointment(request, 1L);

        assertNotNull(response);
        assertEquals(AppointmentStatus.BOOKED, response.getStatus());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void createAppointment_PastTime_ThrowsException() {
        AppointmentRequest request = new AppointmentRequest();
        request.setBarberId(1L);
        request.setServiceIds(Arrays.asList(1L));
        request.setStartTime(LocalDateTime.now().minusHours(1));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> appointmentService.createAppointment(request, 1L));

        assertEquals("Booking available minimum 30 minutes in advance", exception.getMessage());
    }

    @Test
    void createAppointment_BarberNotFound_ThrowsException() {
        AppointmentRequest request = new AppointmentRequest();
        request.setBarberId(999L);
        request.setServiceIds(Arrays.asList(1L));
        request.setStartTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0));

        when(barberRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> appointmentService.createAppointment(request, 1L));

        assertEquals("Barber not found", exception.getMessage());
    }

    @Test
    void createAppointment_ServiceNotFound_ThrowsException() {
        AppointmentRequest request = new AppointmentRequest();
        request.setBarberId(1L);
        request.setServiceIds(Arrays.asList(999L));
        request.setStartTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0));

        when(barberRepository.findById(1L)).thenReturn(Optional.of(testBarber));
        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> appointmentService.createAppointment(request, 1L));

        assertEquals("Service not found: 999", exception.getMessage());
    }

    @Test
    void createAppointment_Collision_ThrowsException() {
        AppointmentRequest request = new AppointmentRequest();
        request.setBarberId(1L);
        request.setServiceIds(Arrays.asList(1L));
        LocalDateTime newStart = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        request.setStartTime(newStart);

        Appointment existingAppointment = new Appointment();
        existingAppointment.setAppointmentId(2L);
        existingAppointment.setBarber(testBarber);
        existingAppointment.setStartTime(newStart);
        existingAppointment.setEndTime(newStart.plusMinutes(30));
        existingAppointment.setStatus(AppointmentStatus.BOOKED);

        when(barberRepository.findById(1L)).thenReturn(Optional.of(testBarber));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
        when(availabilityRepository.findByBarberBarberId(1L)).thenReturn(Arrays.asList(testAvailability));
        when(appointmentRepository.findByBarberBarberIdAndStartTimeAfter(any(), any()))
            .thenReturn(Arrays.asList(existingAppointment));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> appointmentService.createAppointment(request, 1L));

        assertEquals("Time slot is occupied", exception.getMessage());
    }

    @Test
    void getAppointmentsByUser_Success() {
        when(appointmentRepository.findByClientUserId(1L)).thenReturn(Arrays.asList(testAppointment));

        List<AppointmentResponse> responses = appointmentService.getAppointmentsByUser(1L);

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getAppointmentId());
    }

    @Test
    void getAppointmentsByUser_EmptyList() {
        when(appointmentRepository.findByClientUserId(1L)).thenReturn(Collections.emptyList());

        List<AppointmentResponse> responses = appointmentService.getAppointmentsByUser(1L);

        assertTrue(responses.isEmpty());
    }

    @Test
    void cancelAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        appointmentService.cancelAppointment(1L, 1L);

        assertEquals(AppointmentStatus.CANCELLED, testAppointment.getStatus());
        verify(appointmentRepository).save(testAppointment);
    }

    @Test
    void cancelAppointment_NotAuthorized_ThrowsException() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> appointmentService.cancelAppointment(1L, 999L));

        assertEquals("Not authorized to cancel this appointment", exception.getMessage());
    }

    @Test
    void updateStatus_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        AppointmentResponse response = appointmentService.updateStatus(1L, AppointmentStatus.COMPLETED, 2L);

        assertEquals(AppointmentStatus.COMPLETED, testAppointment.getStatus());
    }

    @Test
    void updateStatus_NotAuthorized_ThrowsException() {
        testAppointment.getBarber().getUser().setUserId(999L);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> appointmentService.updateStatus(1L, AppointmentStatus.COMPLETED, 2L));

        assertEquals("Not authorized", exception.getMessage());
    }
}