package gr.odys.ds_backend.controller;

import gr.odys.ds_backend.config.JwtUtils;
import gr.odys.ds_backend.entity.Citizen;
import gr.odys.ds_backend.entity.Role;
import gr.odys.ds_backend.entity.RoleType;
import gr.odys.ds_backend.entity.User;
import gr.odys.ds_backend.entity.UserProfile;
import gr.odys.ds_backend.payload.request.LoginRequest;
import gr.odys.ds_backend.payload.request.SignupRequest;
import gr.odys.ds_backend.payload.response.JwtResponse;
import gr.odys.ds_backend.payload.response.MessageResponse;
import gr.odys.ds_backend.payload.response.ValidationErrorResponse;
import gr.odys.ds_backend.repository.CitizenRepository;
import gr.odys.ds_backend.repository.RoleRepository;
import gr.odys.ds_backend.repository.UserRepository;
import gr.odys.ds_backend.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CitizenRepository citizenRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(null,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Authentication attempt for user: {}", loginRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            logger.info("User {} successfully authenticated", loginRequest.getUsername());
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        } catch (DisabledException e) {
            logger.warn("Failed login attempt: Account disabled for user {}", loginRequest.getUsername());
            ValidationErrorResponse response = new ValidationErrorResponse();
            response.addError("Account is disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            logger.warn("Failed login attempt for user {}: {}", loginRequest.getUsername(), e.getMessage());
            ValidationErrorResponse response = new ValidationErrorResponse();
            response.addError("Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        logger.info("Processing signup request for email: {}", signUpRequest.getEmail());
        ValidationErrorResponse validationErrors = new ValidationErrorResponse();

        // Generate default username if not provided
        String username = signUpRequest.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = (signUpRequest.getFirstName() + "." + signUpRequest.getLastName()).toLowerCase();
            int suffix = 1;
            String baseUsername = username;
            
            // Keep trying with incremented suffix until we find a unique username
            while (userRepository.existsByUsername(username)) {
                username = baseUsername + suffix;
                suffix++;
            }
        } else if (userRepository.existsByUsername(username)) {
            String error = "Username is already taken";
            logger.warn("Signup validation error: {}", error);
            validationErrors.addError(error);
        }

        // Validate email
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            String error = "Email is already registered";
            logger.warn("Signup validation error: {}", error);
            validationErrors.addError(error);
        }

        // Validate password strength
        if (!PASSWORD_PATTERN.matcher(signUpRequest.getPassword()).matches()) {
            String error = "Password must be at least 8 characters long and contain at least one number, one uppercase letter, one lowercase letter, and one special character";
            logger.warn("Signup validation error: {}", error);
            validationErrors.addError(error);
        }

        if (validationErrors.hasErrors()) {
            logger.warn("Signup failed with {} validation errors", validationErrors.getErrors().size());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(validationErrors);
        }

        try {
            // Create entities
            Citizen citizen = new Citizen(
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                null, // Phone is optional
                null  // City is optional for now
            );

            UserProfile userProfile = new UserProfile();
            User user = new User(username,
                    signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword()));

            // Set up roles
            Set<String> strRoles = signUpRequest.getRole();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null || strRoles.isEmpty()) {
                Role userRole = roleRepository.findByName(RoleType.ROLE_USER.name())
                        .orElseThrow(() -> new RuntimeException("Error: Default role not found."));
                roles.add(userRole);
            } else {
                strRoles.forEach(role -> {
                    try {
                        RoleType roleType = RoleType.valueOf("ROLE_" + role.toUpperCase());
                        Role userRole = roleRepository.findByName(roleType.name())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "Invalid role: " + role + ". Valid roles are: user, employee, vet, admin"));
                        roles.add(userRole);
                    } catch (IllegalArgumentException e) {
                        logger.warn("Invalid role specified: {}", role);
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + role);
                    }
                });
            }

            // Set up entity relationships
            userProfile.setCitizen(citizen);      // This will also set citizen.userProfile due to bidirectional handling
            userProfile.setUser(user);            // This will also set user.userProfile due to bidirectional handling
            user.setRoles(roles);
            user.setEnabled(true);

            // Save user which will cascade save userProfile and citizen due to cascade settings
            userRepository.save(user);

            logger.info("User registered successfully: {}", user.getUsername());
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error during user registration", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error registering user: " + e.getMessage());
        }
    }
}