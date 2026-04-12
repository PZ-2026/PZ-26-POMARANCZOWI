package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}