package pl.pomaranczowi.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
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