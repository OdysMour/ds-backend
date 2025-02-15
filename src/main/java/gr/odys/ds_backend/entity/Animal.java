package gr.odys.ds_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "animals")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "MALE|FEMALE", message = "Sex should be MALE or FEMALE")
    private String sex;

    private String name;

    @NotBlank
    @Pattern(regexp = "CAT|DOG", message = "Species should be CAT or DOG")
    private String animalSpecies;

    private String breed;

    @DateTimeFormat
    private Date birthDate;
    
    private Long microchip;

    private String healthStatus;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    @JsonBackReference("animal-userprofile")
    private UserProfile userProfile;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL)
    @JsonManagedReference("animal-healthchecks")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @Basic(fetch = FetchType.LAZY)
    private List<AnimalHealthCheck> healthChecks;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastModifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedAt = LocalDateTime.now();
    }

    public Animal() {
    }

    public Animal(String name, String sex, String animalSpecies, String breed, Date birthDate, Long microchip) {
        this.name = name;
        this.sex = sex;
        this.animalSpecies = animalSpecies;
        this.breed = breed;
        this.birthDate = birthDate;
        this.microchip = microchip;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAnimalSpecies() {
        return animalSpecies;
    }

    public void setAnimalSpecies(String animalSpecies) {
        this.animalSpecies = animalSpecies;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Long getMicrochip() {
        return microchip;
    }

    public void setMicrochip(Long microchip) {
        this.microchip = microchip;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public List<AnimalHealthCheck> getHealthChecks() {
        return healthChecks;
    }

    public void setHealthChecks(List<AnimalHealthCheck> healthChecks) {
        this.healthChecks = healthChecks;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", animalSpecies='" + animalSpecies + '\'' +
                ", breed='" + breed + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", microchip=" + microchip +
                ", healthStatus='" + healthStatus + '\'' +
                "}";
    }
}