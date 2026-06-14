package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.User;
import pl.pomaranczowi.backend.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entity.
 * Provides lookup by email and role.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their email address.
     *
     * @param email the email address
     * @return the user, if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds all users with the specified role.
     *
     * @param role the user role
     * @return list of users with that role
     */
    List<User> findByRole(UserRole role);
}
