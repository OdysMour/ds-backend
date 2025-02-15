package gr.odys.ds_backend.api;

import java.util.List;

import gr.odys.ds_backend.payload.response.CitizenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import gr.odys.ds_backend.entity.Citizen;
import gr.odys.ds_backend.repository.CitizenRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/citizens")
public class CitizenApi {

    @Autowired
    private CitizenRepository citizenRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'VET', 'MANAGER', 'ADMIN')")
    public ResponseEntity<List<CitizenDTO>> getCitizens() {
        try {
            List<Citizen> citizens = citizenRepository.findAll();
            List<CitizenDTO> citizenDTOs = citizens.stream()
                .map(citizen -> new CitizenDTO(citizen))
                .toList();
            return ResponseEntity.ok(citizenDTOs);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving citizens", e);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'VET', 'MANAGER', 'ADMIN')")
    public ResponseEntity<CitizenDTO> getCitizen(@PathVariable Long id) {
        Citizen citizen = citizenRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Citizen not found with id: " + id));
        return ResponseEntity.ok(new CitizenDTO(citizen));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'VET', 'MANAGER', 'ADMIN')")
    public ResponseEntity<CitizenDTO> createCitizen(@Valid @RequestBody Citizen citizen) {
        try {
            // Prevent ID injection for security
            citizen.setId(null);
            
            Citizen savedCitizen = citizenRepository.save(citizen);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CitizenDTO(savedCitizen));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating citizen: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'VET', 'MANAGER', 'ADMIN')")
    public ResponseEntity<CitizenDTO> updateCitizen(@PathVariable Long id, @Valid @RequestBody Citizen citizenDetails) {
        try {
            Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Citizen not found with id: " + id));

            // Update allowed fields
            citizen.setFirstName(citizenDetails.getFirstName());
            citizen.setLastName(citizenDetails.getLastName());
            citizen.setPhone(citizenDetails.getPhone());
            citizen.setCity(citizenDetails.getCity());
            // Don't update animals relationship through this endpoint
            
            Citizen updatedCitizen = citizenRepository.save(citizen);
            return ResponseEntity.ok(new CitizenDTO(updatedCitizen));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating citizen: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCitizen(@PathVariable Long id) {
        try {
            Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Citizen not found with id: " + id));
            
            citizenRepository.delete(citizen);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting citizen: " + e.getMessage());
        }
    }
}