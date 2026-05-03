package pl.pomaranczowi.backend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.pomaranczowi.backend.entity.Barber;
import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.entity.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DatabaseConnectionTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BarberRepository barberRepository;

    @Test
    void userRepository_SaveAndFindByEmail() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPhone("123456789");
        user.setPasswordHash("hashedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(UserRole.CLIENT);

        User saved = userRepository.save(user);
        assertNotNull(saved.getUserId());

        Optional<User> found = userRepository.findByEmail("test@test.com");
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    void userRepository_FindByEmail_NotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@test.com");
        assertTrue(found.isEmpty());
    }

    @Test
    void userRepository_Delete() {
        User user = new User();
        user.setName("Delete Test");
        user.setEmail("delete@test.com");
        user.setPhone("123456789");
        user.setPasswordHash("hashedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(UserRole.CLIENT);

        User saved = userRepository.save(user);
        Long userId = saved.getUserId();

        userRepository.deleteById(userId);

        Optional<User> found = userRepository.findById(userId);
        assertTrue(found.isEmpty());
    }

    @Test
    void barberRepository_SaveAndFind() {
        User barberUser = new User();
        barberUser.setName("Barber Test");
        barberUser.setEmail("barber@test.com");
        barberUser.setPhone("987654321");
        barberUser.setPasswordHash("hashedPassword");
        barberUser.setCreatedAt(LocalDateTime.now());
        barberUser.setRole(UserRole.EMPLOYEE);
        userRepository.save(barberUser);

        Barber barber = new Barber();
        barber.setUser(barberUser);
        barber.setSpecialization("Fade");
        barber.setBio("Expert barber");

        Barber saved = barberRepository.save(barber);
        assertNotNull(saved.getBarberId());

        Optional<Barber> found = barberRepository.findById(saved.getBarberId());
        assertTrue(found.isPresent());
        assertEquals("Fade", found.get().getSpecialization());
    }

    @Test
    void userRepository_Count() {
        long initialCount = userRepository.count();
        
        User user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@test.com");
        user1.setPhone("111111111");
        user1.setPasswordHash("hash1");
        user1.setCreatedAt(LocalDateTime.now());
        user1.setRole(UserRole.CLIENT);
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@test.com");
        user2.setPhone("222222222");
        user2.setPasswordHash("hash2");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setRole(UserRole.CLIENT);
        userRepository.save(user2);

        assertEquals(initialCount + 2, userRepository.count());
    }
}