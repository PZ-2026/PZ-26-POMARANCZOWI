package pl.pomaranczowi.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pomaranczowi.backend.dto.BarberDto;
import pl.pomaranczowi.backend.entity.Barber;
import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.entity.UserRole;
import pl.pomaranczowi.backend.repository.BarberRepository;
import pl.pomaranczowi.backend.repository.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarberServiceTest {

    @Mock
    private BarberRepository barberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BarberService barberService;

    private Barber testBarber;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("John Barber");
        testUser.setEmail("john@barber.com");
        testUser.setPhone("123456789");
        testUser.setRole(UserRole.EMPLOYEE);

        testBarber = new Barber();
        testBarber.setBarberId(1L);
        testBarber.setUser(testUser);
        testBarber.setSpecialization("Fade");
        testBarber.setBio("Expert barber with 5 years experience");
    }

    @Test
    void getAllBarbers_ReturnsList() {
        when(barberRepository.findAll()).thenReturn(Arrays.asList(testBarber));

        List<BarberDto> result = barberService.getAllBarbers();

        assertAll("barber list",
            () -> assertEquals(1, result.size()),
            () -> assertEquals("John Barber", result.get(0).getName()),
            () -> assertEquals("Fade", result.get(0).getSpecialization()),
            () -> assertEquals("john@barber.com", result.get(0).getEmail()),
            () -> assertEquals("123456789", result.get(0).getPhone()),
            () -> assertEquals("Expert barber with 5 years experience", result.get(0).getBio()),
            () -> assertEquals(UserRole.EMPLOYEE, result.get(0).getRole())
        );
    }

    @Test
    void getAllBarbers_EmptyList() {
        when(barberRepository.findAll()).thenReturn(Collections.emptyList());

        List<BarberDto> result = barberService.getAllBarbers();

        assertTrue(result.isEmpty());
    }

    @Test
    void getBarberById_Success() {
        when(barberRepository.findById(1L)).thenReturn(Optional.of(testBarber));

        BarberDto result = barberService.getBarberById(1L);

        assertAll("barber details",
            () -> assertEquals(1L, result.getBarberId()),
            () -> assertEquals("John Barber", result.getName()),
            () -> assertEquals("Fade", result.getSpecialization())
        );
    }

    @Test
    void getBarberById_NotFound_ThrowsException() {
        when(barberRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> barberService.getBarberById(999L));

        assertEquals("Barber not found", exception.getMessage());
    }
}
