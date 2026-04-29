package pl.pomaranczowi.backend.repository;

import pl.pomaranczowi.backend.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}