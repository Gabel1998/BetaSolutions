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

    public double calculateTotalCo2ForProject(int projectId) {
        try {
            double total = resourceRepository.calculateTotalCo2ForProject(projectId);
            return total > 0 ? total : -1; // -1 signals N/A
        } catch (Exception e) {
            return -1;
        }
    }
}
