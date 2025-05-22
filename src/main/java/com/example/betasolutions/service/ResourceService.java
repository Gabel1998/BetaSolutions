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

    // fallback for no resources found
    public double calculateCo2(Resource resource, double hoursWorked) {
        if (resource == null || hoursWorked < 0) {
            return -1;
        }
        return resource.getRe_co2_perHour() * hoursWorked;
    }

    public double calculateTotalCo2ForProject(int projectId) {
        try {
            double total = resourceRepository.calculateTotalCo2ForProject(projectId);
            return total > 0 ? total : -1; // -1 signals N/A
        } catch (Exception e) {
            System.err.println("⚠ Failed CO2 calc: " + e.getMessage());
            return -1;
        }
    }
}
