package gr.odys.ds_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "animals")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "MALE|FEMALE", message = "Sex should be MALE or FEMALE")
    private String sex;

    @NotBlank
    @Pattern(regexp = "CAT|DOG", message = "Species should be CAT or DOG")
    private String animalSpecies;

    @NotBlank
    @DateTimeFormat
    private Date birthDate;

    @NotBlank
    @Size(min = 10, max = 10)
    private Long microchip;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="citizen_id")
    private Citizen citizen;

    @OneToMany(mappedBy = "animal")
    private List<AnimalHealthCheck> healthChecks;

    public Animal() {
    }

    public Animal(String sex, String animalSpecies, Date birthDate, Long microchip) {
        this.sex = sex;
        this.animalSpecies = animalSpecies;
        this.birthDate = birthDate;
        this.microchip = microchip;
    }

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

    public String getAnimalSpecies() {
        return animalSpecies;
    }

    public void setAnimalSpecies(String animalSpecies) {
        this.animalSpecies = animalSpecies;
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

    public Citizen getCitizen() {
        return citizen;
    }

    public void setCitizen(Citizen citizen) {
        this.citizen = citizen;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", sex='" + sex + '\'' +
                ", animalSpecies='" + animalSpecies + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", microchip=" + microchip +
                ", citizen=" + citizen +
                '}';
    }
}