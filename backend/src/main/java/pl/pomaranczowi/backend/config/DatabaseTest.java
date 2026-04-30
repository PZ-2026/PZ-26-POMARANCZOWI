package pl.pomaranczowi.backend.config;

import pl.pomaranczowi.backend.entity.*;
import pl.pomaranczowi.backend.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
public class DatabaseTest {

    @Bean
    public org.springframework.boot.CommandLineRunner testDb(
            UserRepository userRepo,
            BarberRepository barberRepo,
            ServiceRepository serviceRepo,
            AvailabilityRepository availabilityRepo,
            BarberServiceRepository barberServiceRepo) {
        return args -> {

            if (userRepo.count() > 0) {
                System.out.println("Dane już istnieją w bazie, pomijam...");
                return;
            }

            User admin = new User(null, "Admin User", "admin@test.com", "111111111", "hash1", LocalDateTime.now(),
                    UserRole.ADMIN);
            User barber1 = new User(null, "Barber One", "barber1@test.com", "222222222", "hash2", LocalDateTime.now(),
                    UserRole.EMPLOYEE);
            User client1 = new User(null, "Client One", "client1@test.com", "333333333", "hash3", LocalDateTime.now(),
                    UserRole.CLIENT);

            admin = userRepo.save(admin);
            barber1 = userRepo.save(barber1);
            client1 = userRepo.save(client1);

            Barber barber = new Barber(null, barber1, "Fade cuts",
                    "Experienced barber specializing in fades and modern cuts");
            barber = barberRepo.save(barber);

            Service s1 = new Service(null, "Haircut", "Basic haircut", 30, 50.0, true);
            Service s2 = new Service(null, "Beard Trim", "Beard shaping", 20, 30.0, true);
            s1 = serviceRepo.save(s1);
            s2 = serviceRepo.save(s2);

            BarberService bs1 = new BarberService(null, barber, s1);
            BarberService bs2 = new BarberService(null, barber, s2);
            barberServiceRepo.save(bs1);
            barberServiceRepo.save(bs2);

            for (int day = 1; day <= 7; day++) {
                Availability avail = new Availability(null, barber, day, LocalTime.of(9, 0), LocalTime.of(17, 0));
                availabilityRepo.save(avail);
            }

            System.out.println("Użytkownicy: " + userRepo.count());
            System.out.println("Barberzy: " + barberRepo.count());
            System.out.println("Usługi: " + serviceRepo.count());
        };
    }
}