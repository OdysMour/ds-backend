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

import gr.odys.ds_backend.entity.Citizen;
import gr.odys.ds_backend.entity.UserProfile;
import gr.odys.ds_backend.repository.CitizenRepository;
import gr.odys.ds_backend.repository.UserProfileRepository;
import gr.odys.ds_backend.service.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping({/* "/api/profiles",  */"/api/user-profiles"})
public class UserProfileApi {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CitizenRepository citizenRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfile>> getAllProfiles() {
        try {
            List<UserProfile> profiles = userProfileRepository.findAll();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving profiles", e);
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'EMPLOYEE', 'VET', 'MANAGER', 'ADMIN')")
    public ResponseEntity<UserProfile> getCurrentUserProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            
            UserProfile profile = userProfileRepository.findByIdWithAnimals(userDetails.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
            
            return ResponseEntity.ok(profile);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving profile", e);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfile> getProfile(@PathVariable Long id) {
        UserProfile profile = userProfileRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found with id: " + id));
        return ResponseEntity.ok(profile);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfile> createProfile(@Valid @RequestBody UserProfile profile) {
        try {
            // Security measures
            profile.setId(null); // Prevent ID injection
            
            UserProfile savedProfile = userProfileRepository.save(profile);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProfile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating profile: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/citizen/{citizenId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfile> linkCitizen(@PathVariable Long id, @PathVariable Long citizenId) {
        try {
            UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found with id: " + id));
            
            Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Citizen not found with id: " + citizenId));

            // Check if citizen is already linked to another profile
            if (profile.getCitizen() != null && !profile.getCitizen().getId().equals(citizenId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile already linked to a different citizen");
            }

            profile.setCitizen(citizen);
            UserProfile updatedProfile = userProfileRepository.save(profile);
            return ResponseEntity.ok(updatedProfile);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error linking citizen to profile: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/citizen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfile> unlinkCitizen(@PathVariable Long id) {
        try {
            UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found with id: " + id));
            
            profile.setCitizen(null);
            UserProfile updatedProfile = userProfileRepository.save(profile);
            return ResponseEntity.ok(updatedProfile);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error unlinking citizen from profile: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        try {
            UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found with id: " + id));
            
            userProfileRepository.delete(profile);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting profile: " + e.getMessage());
        }
    }
}