package com.example.betasolutions.controller;
import com.example.betasolutions.model.Resource;
import com.example.betasolutions.repository.ResourceRepository;
import com.example.betasolutions.service.ResourceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

    @Controller
    @RequestMapping("/resources")
    public class ResourceController {

        private final ResourceRepository repository;
        private final ResourceService service;

        public ResourceController(ResourceRepository repository, ResourceService service) {
            this.repository = repository;
            this.service = service;
        }

        @GetMapping
        public List<Resource> getAllResources() {
            return repository.findAll();
        }

        @PostMapping
        public void addResource(@RequestBody Resource resource) {
            repository.save(resource);
        }

        @GetMapping("/{id}/co2")
        public double calculateCo2ForResource(@PathVariable int id, @RequestParam double hours) {
            Resource resource = repository.findById(id);
            return service.calculateCo2(resource, hours);
        }
    }
