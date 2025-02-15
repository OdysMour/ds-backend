package gr.odys.ds_backend.api;

import java.util.List;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import gr.odys.ds_backend.entity.Animal;
import gr.odys.ds_backend.entity.AnimalHealthCheck;
import gr.odys.ds_backend.repository.AnimalHealthCheckRepository;
import gr.odys.ds_backend.repository.AnimalRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/health-checks")
public class AnimalHealthCheckApi {

    @Autowired
    private AnimalHealthCheckRepository healthCheckRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('VET', 'MANAGER', 'ADMIN')")
    public ResponseEntity<List<AnimalHealthCheck>> getHealthChecks() {
        try {
            List<AnimalHealthCheck> healthChecks = healthCheckRepository.findAll();
            return ResponseEntity.ok(healthChecks);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving health checks", e);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VET', 'MANAGER', 'ADMIN')")
    public ResponseEntity<AnimalHealthCheck> getHealthCheck(@PathVariable Long id) {
        AnimalHealthCheck healthCheck = healthCheckRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check not found with id: " + id));
        return ResponseEntity.ok(healthCheck);
    }

    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasAnyRole('VET', 'MANAGER', 'ADMIN')")
    public ResponseEntity<List<AnimalHealthCheck>> getHealthChecksByAnimal(@PathVariable Long animalId) {
        try {
            Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal not found with id: " + animalId));
            
            List<AnimalHealthCheck> healthChecks = healthCheckRepository.findByAnimal(animal);
            return ResponseEntity.ok(healthChecks);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving health checks for animal", e);
        }
    }

    @PostMapping("/animal/{animalId}")
    @PreAuthorize("hasAnyRole('VET', 'ADMIN')")
    public ResponseEntity<AnimalHealthCheck> createHealthCheck(
            @PathVariable Long animalId,
            @Valid @RequestBody AnimalHealthCheck healthCheck) {
        try {
            Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal not found with id: " + animalId));

            // Security measures
            healthCheck.setId(null); // Prevent ID injection
            healthCheck.setCreated(new Date()); // Set current date
            healthCheck.setAnimal(animal);
            
            AnimalHealthCheck savedHealthCheck = healthCheckRepository.save(healthCheck);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedHealthCheck);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating health check: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VET', 'ADMIN')")
    public ResponseEntity<AnimalHealthCheck> updateHealthCheck(
            @PathVariable Long id,
            @Valid @RequestBody AnimalHealthCheck healthCheckDetails) {
        try {
            AnimalHealthCheck healthCheck = healthCheckRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check not found with id: " + id));

            // Update only the description field
            healthCheck.setDescription(healthCheckDetails.getDescription());
            // Don't update created date or animal relationship
            
            AnimalHealthCheck updatedHealthCheck = healthCheckRepository.save(healthCheck);
            return ResponseEntity.ok(updatedHealthCheck);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating health check: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHealthCheck(@PathVariable Long id) {
        try {
            AnimalHealthCheck healthCheck = healthCheckRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check not found with id: " + id));
            
            healthCheckRepository.delete(healthCheck);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting health check: " + e.getMessage());
        }
    }
}