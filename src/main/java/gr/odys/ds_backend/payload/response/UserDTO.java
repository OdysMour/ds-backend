package gr.odys.ds_backend.payload.response;

import gr.odys.ds_backend.entity.Citizen;
import gr.odys.ds_backend.entity.Role;
import gr.odys.ds_backend.entity.User;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private Set<String> roles;
    private Long profileId;
    private String citizenName;

    public UserDTO() {}

    public static UserDTO fromUser(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());
        
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .map(name -> name.replace("ROLE_", ""))
                .collect(Collectors.toSet()));
        }
        
        if (user.getUserProfile() != null) {
            dto.setProfileId(user.getUserProfile().getId());
            if (user.getUserProfile().getCitizen() != null) {
                Citizen citizen = user.getUserProfile().getCitizen();
                dto.setCitizenName(citizen.getFirstName() + " " + citizen.getLastName());
            }
        }
        
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public void setCitizenName(String citizenName) {
        this.citizenName = citizenName;
    }
}