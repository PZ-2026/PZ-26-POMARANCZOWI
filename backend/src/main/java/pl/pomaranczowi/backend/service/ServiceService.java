package pl.pomaranczowi.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.pomaranczowi.backend.dto.ServiceDto;
import pl.pomaranczowi.backend.repository.ServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for browsing the service catalog.
 * Provides methods to list all services, get a service by ID,
 * and retrieve the most popular services.
 */
@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    /**
     * Retrieves all available services.
     *
     * @return list of all service DTOs
     */
    public List<ServiceDto> getAllServices() {
        List<pl.pomaranczowi.backend.entity.Service> services = serviceRepository.findAll();
        return services.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single service by its ID.
     *
     * @param id the service ID
     * @return the service DTO
     * @throws RuntimeException if the service is not found
     */
    public ServiceDto getServiceById(Long id) {
        pl.pomaranczowi.backend.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        return mapToDto(service);
    }

    /**
     * Retrieves the top N most popular services based on appointment booking count.
     *
     * @param limit the maximum number of services to return
     * @return list of popular service DTOs
     */
    public List<ServiceDto> getPopularServices(int limit) {
        List<pl.pomaranczowi.backend.entity.Service> services =
                serviceRepository.findTopPopularServices(PageRequest.of(0, limit));
        return services.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ServiceDto createService(ServiceDto serviceDto) {
        pl.pomaranczowi.backend.entity.Service service = new pl.pomaranczowi.backend.entity.Service();
        service.setName(serviceDto.getName());
        service.setDescription(serviceDto.getDescription());
        service.setDurationMinutes(serviceDto.getDurationMinutes());
        service.setPrice(serviceDto.getPrice());
        service.setIsActive(serviceDto.getIsActive());
        
        pl.pomaranczowi.backend.entity.Service savedService = serviceRepository.save(service);
        return mapToDto(savedService);
    }

    public ServiceDto updateService(Long id, ServiceDto serviceDto) {
        pl.pomaranczowi.backend.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setName(serviceDto.getName());
        service.setDescription(serviceDto.getDescription());
        service.setDurationMinutes(serviceDto.getDurationMinutes());
        service.setPrice(serviceDto.getPrice());
        service.setIsActive(serviceDto.getIsActive());

        pl.pomaranczowi.backend.entity.Service updatedService = serviceRepository.save(service);
        return mapToDto(updatedService);
    }

    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new RuntimeException("Service not found");
        }
        serviceRepository.deleteById(id);
    }

    /**
     * Maps a Service entity to its DTO representation.
     *
     * @param service the service entity
     * @return the corresponding DTO
     */
    private ServiceDto mapToDto(pl.pomaranczowi.backend.entity.Service service) {
        return new ServiceDto(
                service.getServiceId(),
                service.getName(),
                service.getDescription(),
                service.getDurationMinutes(),
                service.getPrice(),
                service.getIsActive()
        );
    }
}
