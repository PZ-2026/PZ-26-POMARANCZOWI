package pl.pomaranczowi.backend.config;

import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.entity.UserRole;
import pl.pomaranczowi.backend.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseTest {

    @Bean
    public org.springframework.boot.CommandLineRunner testDb(UserRepository repo) {
        return args -> {

            User user = new User(
                    null,
                    "test@test.com",
                    "1234",
                    UserRole.CLIENT
            );

            repo.save(user);

            repo.findAll().forEach(u ->
                    System.out.println(u.getEmail())
            );
        };
    }
}