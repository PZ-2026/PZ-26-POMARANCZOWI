package pl.pomaranczowi.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pomaranczowi.backend.dto.AvailabilityDto;
import pl.pomaranczowi.backend.entity.Availability;
import pl.pomaranczowi.backend.entity.Barber;
import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.entity.UserRole;
import pl.pomaranczowi.backend.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private BarberRepository barberRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    private User adminUser;
    private User barberUser;
    private Barber testBarber;
    private Availability testAvailability;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setUserId(1L);
        adminUser.setName("Admin");
        adminUser.setRole(UserRole.ADMIN);

        barberUser = new User();
        barberUser.setUserId(2L);
        barberUser.setName("Barber");
        barberUser.setRole(UserRole.EMPLOYEE);

        testBarber = new Barber();
        testBarber.setBarberId(1L);
        testBarber.setUser(barberUser);
        testBarber.setSpecialization("Fade");
        testBarber.setBio("Expert barber");

        testAvailability = new Availability();
        testAvailability.setAvailabilityId(1L);
        testAvailability.setBarber(testBarber);
        testAvailability.setDayOfWeek(1);
        testAvailability.setStartTime(LocalTime.of(9, 0));
        testAvailability.setEndTime(LocalTime.of(17, 0));
    }

    @Test
    void getAvailabilityByBarber_ReturnsList() {
        when(availabilityRepository.findByBarberBarberId(1L)).thenReturn(Arrays.asList(testAvailability));

        List<AvailabilityDto> result = availabilityService.getAvailabilityByBarber(1L);

        assertAll("availability list",
                () -> assertEquals(1, result.size()),
                () -> assertEquals(1L, result.get(0).getAvailabilityId()),
                () -> assertEquals(1, result.get(0).getDayOfWeek()),
                () -> assertEquals(LocalTime.of(9, 0), result.get(0).getStartTime()),
                () -> assertEquals(LocalTime.of(17, 0), result.get(0).getEndTime()));
    }

    @Test
    void getAvailabilityByBarber_EmptyList() {
        when(availabilityRepository.findByBarberBarberId(1L)).thenReturn(Collections.emptyList());

        List<AvailabilityDto> result = availabilityService.getAvailabilityByBarber(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void createAvailability_AsAdmin_Success() {
        AvailabilityDto dto = new AvailabilityDto(null, 1L, 1, LocalTime.of(9, 0), LocalTime.of(17, 0));

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(barberRepository.findById(1L)).thenReturn(Optional.of(testBarber));
        when(availabilityRepository.save(any(Availability.class))).thenReturn(testAvailability);

        AvailabilityDto result = availabilityService.createAvailability(dto, 1L);

        assertAll("admin creates availability",
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.getAvailabilityId()),
                () -> assertEquals(LocalTime.of(9, 0), result.getStartTime()));
    }

    @Test
    void createAvailability_AsAdmin_MissingBarberId_ThrowsException() {
        AvailabilityDto dto = new AvailabilityDto(null, null, 1, LocalTime.of(9, 0), LocalTime.of(17, 0));

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> availabilityService.createAvailability(dto, 1L));

        assertEquals("Barber ID required for admin", exception.getMessage());
        verify(availabilityRepository, never()).save(any());
    }

    @Test
    void createAvailability_AsBarber_Success() {
        AvailabilityDto dto = new AvailabilityDto(null, null, 1, LocalTime.of(9, 0), LocalTime.of(17, 0));

        when(userRepository.findById(2L)).thenReturn(Optional.of(barberUser));
        when(barberRepository.findByUserUserId(2L)).thenReturn(testBarber);
        when(availabilityRepository.save(any(Availability.class))).thenReturn(testAvailability);

        AvailabilityDto result = availabilityService.createAvailability(dto, 2L);

        assertNotNull(result);
        verify(barberRepository).findByUserUserId(2L);
    }

    @Test
    void createAvailability_AsNonBarber_ThrowsException() {
        User clientUser = new User();
        clientUser.setUserId(3L);
        clientUser.setRole(UserRole.CLIENT);

        AvailabilityDto dto = new AvailabilityDto(null, null, 1, LocalTime.of(9, 0), LocalTime.of(17, 0));

        when(userRepository.findById(3L)).thenReturn(Optional.of(clientUser));
        when(barberRepository.findByUserUserId(3L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> availabilityService.createAvailability(dto, 3L));

        assertEquals("You are not a barber", exception.getMessage());
        verify(availabilityRepository, never()).save(any());
    }

    @Test
    void updateAvailability_AsAdmin_Success() {
        AvailabilityDto dto = new AvailabilityDto(null, null, 2, LocalTime.of(10, 0), LocalTime.of(16, 0));

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(testAvailability));
        when(availabilityRepository.save(any(Availability.class))).thenReturn(testAvailability);

        AvailabilityDto result = availabilityService.updateAvailability(1L, dto, 1L);

        assertAll("admin updates availability",
                () -> assertNotNull(result),
                () -> assertEquals(2, testAvailability.getDayOfWeek()),
                () -> assertEquals(LocalTime.of(10, 0), testAvailability.getStartTime()),
                () -> assertEquals(LocalTime.of(16, 0), testAvailability.getEndTime()));
    }

    @Test
    void updateAvailability_NotAuthorized_ThrowsException() {
        User otherUser = new User();
        otherUser.setUserId(5L);
        otherUser.setRole(UserRole.CLIENT);

        AvailabilityDto dto = new AvailabilityDto(null, null, 2, LocalTime.of(10, 0), LocalTime.of(16, 0));

        when(userRepository.findById(5L)).thenReturn(Optional.of(otherUser));
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(testAvailability));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> availabilityService.updateAvailability(1L, dto, 5L));

        assertEquals("Not authorized", exception.getMessage());
    }

    @Test
    void deleteAvailability_AsOwner_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(barberUser));
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(testAvailability));

        assertDoesNotThrow(() -> availabilityService.deleteAvailability(1L, 2L));

        verify(availabilityRepository).delete(testAvailability);
    }

    @Test
    void deleteAvailability_NotAuthorized_ThrowsException() {
        User otherUser = new User();
        otherUser.setUserId(3L);
        otherUser.setRole(UserRole.CLIENT);

        when(userRepository.findById(3L)).thenReturn(Optional.of(otherUser));
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(testAvailability));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> availabilityService.deleteAvailability(1L, 3L));

        assertEquals("Not authorized", exception.getMessage());
        verify(availabilityRepository, never()).delete(any());
    }

    @Test
    void getAvailableTimes_ReturnsSlots() {
        LocalDate monday = LocalDate.of(2026, 6, 15);
        when(availabilityRepository.findByBarberBarberIdAndDayOfWeek(1L, 1))
                .thenReturn(Optional.of(testAvailability));
        when(appointmentRepository.findByBarberBarberIdAndStartTimeBetween(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<String> slots = availabilityService.getAvailableTimes(1L, monday, Duration.ofMinutes(30));

        assertAll("available time slots",
                () -> assertFalse(slots.isEmpty()),
                () -> assertTrue(slots.get(0).equals("09:00")));
    }

    @Test
    void getAvailableTimes_NoAvailability_ThrowsException() {
        LocalDate sunday = LocalDate.of(2026, 6, 14);
        when(availabilityRepository.findByBarberBarberIdAndDayOfWeek(1L, 7))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> availabilityService.getAvailableTimes(1L, sunday, Duration.ofMinutes(30)));

        assertEquals("No availability for this day", exception.getMessage());
    }
}
