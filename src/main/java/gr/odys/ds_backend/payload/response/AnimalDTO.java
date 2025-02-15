package gr.odys.ds_backend.payload.response;

import java.time.LocalDateTime;
import java.util.Date;
import gr.odys.ds_backend.entity.Animal;

public class AnimalDTO {
    private Long id;
    private String sex;
    private String name;
    private String animalSpecies;
    private String breed;
    private Date birthDate;
    private Long microchip;
    private String healthStatus;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedAt;
    private Long userProfileId;

    // Default constructor
    public AnimalDTO() {}

    // Constructor from Animal entity
    public AnimalDTO(Animal animal) {
        this.id = animal.getId();
        this.sex = animal.getSex();
        this.name = animal.getName();
        this.animalSpecies = animal.getAnimalSpecies();
        this.breed = animal.getBreed();
        this.birthDate = animal.getBirthDate();
        this.microchip = animal.getMicrochip();
        this.healthStatus = animal.getHealthStatus();
        this.createdBy = animal.getCreatedBy();
        this.createdAt = animal.getCreatedAt();
        this.lastModifiedBy = animal.getLastModifiedBy();
        this.lastModifiedAt = animal.getLastModifiedAt();
        this.userProfileId = animal.getUserProfile() != null ? animal.getUserProfile().getId() : null;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }
}