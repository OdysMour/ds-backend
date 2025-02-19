package gr.odys.ds_backend.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import gr.odys.ds_backend.entity.Animal;
import gr.odys.ds_backend.entity.UserProfile;
import gr.odys.ds_backend.payload.response.AnimalDTO;
import gr.odys.ds_backend.repository.AnimalRepository;
import gr.odys.ds_backend.repository.UserProfileRepository;
import gr.odys.ds_backend.service.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/animals")
public class AnimalApi {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'EMPLOYEE', 'VET', 'MANAGER', 'ADMIN')")
    public ResponseEntity<List<AnimalDTO>> getAnimals() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            List<Animal> animals;

            // Admins and vets see all animals, others see only their own
            if (auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                 a.getAuthority().equals("ROLE_VET"))) {
                animals = animalRepository.findAll();
            } else {
                UserProfile userProfile = userProfileRepository.findByUserId(userDetails.getId());
                animals = animalRepository.findByUserProfile(userProfile);
            }

            List<AnimalDTO> animalDTOs = animals.stream()
                .map(AnimalDTO::new)
                .toList();
            return ResponseEntity.ok(animalDTOs);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving animals", e);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'EMPLOYEE', 'VET', 'MANAGER', 'ADMIN')")
    public ResponseEntity<AnimalDTO> getAnimal(@PathVariable Long id) {
        Animal animal = animalRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal not found with id: " + id));
        return ResponseEntity.ok(new AnimalDTO(animal));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'EMPLOYEE', 'VET', 'MANAGER', 'ADMIN')")
    public ResponseEntity<AnimalDTO> createAnimal(@Valid @RequestBody Animal animal) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            
            // Get a reference to the UserProfile without fully loading it
            Long userProfileId = userProfileRepository.findByUserId(userDetails.getId()).getId();
            UserProfile userProfile = userProfileRepository.getReferenceById(userProfileId);
            
            // Security measures and setup
            animal.setId(null); // Prevent ID injection
            animal.setCreatedBy(userDetails.getUsername());
            animal.setLastModifiedBy(userDetails.getUsername());
            animal.setUserProfile(userProfile);
            
            // Set default health status if none is provided
            if (animal.getHealthStatus() == null || animal.getHealthStatus().trim().isEmpty()) {
                animal.setHealthStatus("Pending initial health check");
            }
            
            Animal savedAnimal = animalRepository.save(animal);
            return ResponseEntity.status(HttpStatus.CREATED).body(new AnimalDTO(savedAnimal));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating animal: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VET', 'MANAGER', 'ADMIN')")
    public ResponseEntity<AnimalDTO> updateAnimal(@PathVariable Long id, @Valid @RequestBody Animal animalDetails) {
        try {
            Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal not found with id: " + id));

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

            // Get a reference to the UserProfile without fully loading it
            Long userProfileId = userProfileRepository.findByUserId(userDetails.getId()).getId();
            UserProfile userProfile = userProfileRepository.getReferenceById(userProfileId);

            // Update only non-null fields
            if (animalDetails.getName() != null) animal.setName(animalDetails.getName());
            if (animalDetails.getAnimalSpecies() != null) animal.setAnimalSpecies(animalDetails.getAnimalSpecies());
            if (animalDetails.getBreed() != null) animal.setBreed(animalDetails.getBreed());
            if (animalDetails.getSex() != null) animal.setSex(animalDetails.getSex());
            if (animalDetails.getBirthDate() != null) animal.setBirthDate(animalDetails.getBirthDate());
            if (animalDetails.getMicrochip() != null) animal.setMicrochip(animalDetails.getMicrochip());
            if (animalDetails.getHealthStatus() != null) animal.setHealthStatus(animalDetails.getHealthStatus());
            
            // Always update last modified by
            animal.setLastModifiedBy(userDetails.getUsername());
            
            Animal updatedAnimal = animalRepository.save(animal);
            return ResponseEntity.ok(new AnimalDTO(updatedAnimal));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating animal: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        try {
            Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal not found with id: " + id));
            
            animalRepository.delete(animal);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting animal: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/health-status")
    @PreAuthorize("hasAnyRole('VET', 'ADMIN')")
    public ResponseEntity<AnimalDTO> updateHealthStatus(
            @PathVariable Long id,
            @RequestBody String healthStatus) {
        try {
            Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal not found with id: " + id));

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

            animal.setHealthStatus(healthStatus);
            animal.setLastModifiedBy(userDetails.getUsername());
            
            Animal updatedAnimal = animalRepository.save(animal);
            return ResponseEntity.ok(new AnimalDTO(updatedAnimal));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating health status: " + e.getMessage());
        }
    }
}
