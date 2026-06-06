package pl.pomaranczowi.backend.config;

import pl.pomaranczowi.backend.entity.*;
import pl.pomaranczowi.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Configuration that seeds the database with sample data for development and testing.
 * Creates admin, barber, and client users; sample services; barber availability
 * schedules; and a sample appointment.
 * <p>
 * This only runs when the database is empty.
 */
@Configuration
public class DatabaseTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Seeds initial data into the database on application startup if no users exist.
     * Creates:
     * <ul>
     *   <li>4 users: admin, 2 barbers, 1 client</li>
     *   <li>2 barber profiles with specializations</li>
     *   <li>4 services (Haircut, Beard Trim, Shave, Combo)</li>
     *   <li>Daily availability (9:00-17:00) for both barbers</li>
     *   <li>1 sample appointment on 2026-06-22 at 11:00</li>
     * </ul>
     *
     * @param userRepo                user repository
     * @param barberRepo              barber repository
     * @param serviceRepo             service repository
     * @param availabilityRepo        availability repository
     * @param barberServiceRepo       barber-service join repository
     * @param appointmentServiceRepo  appointment-service join repository
     * @param appointmentRepo         appointment repository
     * @return a command line runner that executes the seeding logic
     */
    @Bean
    public org.springframework.boot.CommandLineRunner testDb(
            UserRepository userRepo,
            BarberRepository barberRepo,
            ServiceRepository serviceRepo,
            AvailabilityRepository availabilityRepo,
            BarberServiceRepository barberServiceRepo,
            AppointmentServiceRepository appointmentServiceRepo,
            AppointmentRepository appointmentRepo) {
        return args -> {

            if (userRepo.count() > 0) {
                System.out.println("Dane już istnieją w bazie, pomijam...");
                return;
            }

            User admin = new User(null, "Admin User", "admin@test.com", "111111111", passwordEncoder.encode("password1"), LocalDateTime.now(),
                    UserRole.ADMIN);
            User barber1 = new User(null, "Barber One", "barber1@test.com", "222222222", passwordEncoder.encode("password2"), LocalDateTime.now(),
                    UserRole.EMPLOYEE);
            User client1 = new User(null, "Client One", "client1@test.com", "333333333", passwordEncoder.encode("password3"), LocalDateTime.now(),
                    UserRole.CLIENT);
            User barber2 = new User(null, "Barber Two", "barber2@test.com", "444444444", passwordEncoder.encode("password4"), LocalDateTime.now(),
                    UserRole.EMPLOYEE);

            admin = userRepo.save(admin);
            barber1 = userRepo.save(barber1);
            client1 = userRepo.save(client1);
            barber2 = userRepo.save(barber2);

            Barber barber = new Barber(null, barber1, "Fade cuts",
                    "Experienced barber specializing in fades and modern cuts");
            Barber barber2Entity = new Barber(null, barber2, "Classic cuts",
                    "Skilled in classic and traditional barbering techniques");
            barber = barberRepo.save(barber);
            barber2Entity = barberRepo.save(barber2Entity);

            Service s1 = new Service(null, "Haircut", "Basic haircut", 30, 50.0, true);
            Service s2 = new Service(null, "Beard Trim", "Beard shaping", 30, 30.0, true);
            Service s3 = new Service(null, "Shave", "Traditional straight razor shave", 30, 40.0, true);
            Service s4 = new Service(null, "Haircut + Beard Trim", "Combo of haircut and beard trim", 30, 70.0, true);
            s1 = serviceRepo.save(s1);
            s2 = serviceRepo.save(s2);
            s3 = serviceRepo.save(s3);
            s4 = serviceRepo.save(s4);

            BarberService bs1 = new BarberService(null, barber, s1);
            BarberService bs2 = new BarberService(null, barber, s2);
            BarberService bs3 = new BarberService(null, barber, s3);
            BarberService bs4 = new BarberService(null, barber, s4);
            barberServiceRepo.save(bs1);
            barberServiceRepo.save(bs2);
            barberServiceRepo.save(bs3);
            barberServiceRepo.save(bs4);

            for (int day = 1; day <= 7; day++) {
                Availability avail = new Availability(null, barber, day, LocalTime.of(9, 0), LocalTime.of(17, 0));
                availabilityRepo.save(avail);
            }
            for (int day = 1; day <= 7; day++) {
                Availability avail = new Availability(null, barber2Entity, day, LocalTime.of(9, 0), LocalTime.of(17, 0));
                availabilityRepo.save(avail);
            }

            Appointment appt1 = new Appointment(
                    null,
                    client1,
                    barber,
                    LocalDateTime.of(2026, 6, 22, 11, 0),
                    LocalDateTime.of(2026, 6, 22, 11, 30),
                    LocalDateTime.now(),
                    AppointmentStatus.BOOKED
            );
            appointmentRepo.save(appt1);

            AppointmentService apptService = new AppointmentService(null, appt1, s1);
            appointmentServiceRepo.save(apptService);

            System.out.println("Użytkownicy: " + userRepo.count());
            System.out.println("Barberzy: " + barberRepo.count());
            System.out.println("Usługi: " + serviceRepo.count());
            System.out.println("Appointments: " + appointmentRepo.count());
        };
    }
}
