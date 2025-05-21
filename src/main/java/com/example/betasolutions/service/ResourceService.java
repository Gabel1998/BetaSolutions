package com.example.betasolutions.service;

import com.example.betasolutions.model.Resource;
import com.example.betasolutions.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public double calculateCo2(Resource resource, double hoursWorked) {
        resource.setRe_co2_perHour(8.75);
        return resource.getRe_co2_perHour() * hoursWorked;
    }

    public double calculateTotalCo2ForProject(int projectId) {
        return resourceRepository.calculateTotalCo2ForProject(projectId);
    }
}
