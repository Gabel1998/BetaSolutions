package com.example.betasolutions.service;

import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.repository.SubProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Service-lag for SubProject – håndterer logik mellem controller og repository
@Service
public class SubProjectService {

    private final SubProjectRepository subProjectRepository;

    public SubProjectService(SubProjectRepository subProjectRepository) {
        this.subProjectRepository = subProjectRepository;
    }

    public void createSubProject(SubProject subProject) {
        subProjectRepository.save(subProject);
    }

    public List<SubProject> getAllSubProjects() {
        return subProjectRepository.findAll();
    }

    public Optional<SubProject> getSubProjectById(Integer id) {
        return subProjectRepository.findById(id);
    }

    public void updateSubProject(SubProject subProject) {
        subProjectRepository.update(subProject);
    }

    public void deleteSubProject(Integer id) {
        subProjectRepository.delete(id);
    }
}
