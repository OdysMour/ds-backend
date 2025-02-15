package gr.odys.ds_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "user_profiles")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "userProfile")
    @JsonBackReference("user-profile")
    private User user;

    @NotNull(message = "Citizen cannot be null")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "citizen_id", nullable = false)
    @JsonBackReference("citizen-profile")
    private Citizen citizen;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("animal-userprofile")
    private List<Animal> animals;

    public UserProfile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Citizen getCitizen() {
        return citizen;
    }

    public void setCitizen(Citizen citizen) {
        this.citizen = citizen;
        if (citizen != null && citizen.getUserProfile() != this) {
            citizen.setUserProfile(this);
        }
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null && user.getUserProfile() != this) {
            user.setUserProfile(this);
        }
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", citizenId=" + (citizen != null ? citizen.getId() : null) +
                '}';
    }
}
