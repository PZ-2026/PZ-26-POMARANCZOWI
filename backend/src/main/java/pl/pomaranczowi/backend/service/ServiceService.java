package pl.pomaranczowi.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.pomaranczowi.backend.dto.ServiceDto;
import pl.pomaranczowi.backend.repository.ServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<ServiceDto> getAllServices() {
        List<pl.pomaranczowi.backend.entity.Service> services = serviceRepository.findAll();
        return services.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ServiceDto getServiceById(Long id) {
        pl.pomaranczowi.backend.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        return mapToDto(service);
    }

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