package gr.odys.ds_backend.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import gr.odys.ds_backend.payload.response.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import gr.odys.ds_backend.entity.Role;
import gr.odys.ds_backend.entity.User;
import gr.odys.ds_backend.repository.RoleRepository;
import gr.odys.ds_backend.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserApi {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<User> users = userRepository.findAllWithProfiles();
            List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving users", e);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userRepository.findByIdWithProfile(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserRoles(@PathVariable Long id, @RequestBody Set<String> roles) {
        try {
            User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

            Set<Role> newRoles = new HashSet<>();
            roles.forEach(role -> {
                Role userRole = roleRepository.findByName("ROLE_" + role.toUpperCase())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + role));
                newRoles.add(userRole);
            });

            // Ensure at least one role is assigned
            if (newRoles.isEmpty()) {
                Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Default role not found."));
                newRoles.add(userRole);
            }

            user.setRoles(newRoles);
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating user roles: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserPassword(@PathVariable Long id, @RequestBody String newPassword) {
        try {
            User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

            user.setPassword(encoder.encode(newPassword));
            userRepository.save(user);
            
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating password: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id, @RequestBody boolean enabled) {
        try {
            User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

            user.setEnabled(enabled);
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating user status: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
            
            userRepository.delete(user);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting user: " + e.getMessage());
        }
    }
}