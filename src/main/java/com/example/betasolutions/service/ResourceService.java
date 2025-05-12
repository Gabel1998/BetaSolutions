package com.example.betasolutions.service;

import com.example.betasolutions.model.Resource;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {

    // udregningen er lavet ud fra at vi bruger en computer + skærm og derved er co2 per time er 8,75
    public double calculateCo2(Resource resource, double hoursUsed) {
        resource.setRe_co2_perHour(8.75);
        return resource.getRe_co2_perHour() * hoursUsed;
    }

}
