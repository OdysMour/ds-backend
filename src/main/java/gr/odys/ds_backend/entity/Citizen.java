package gr.odys.ds_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "citizens")
public class Citizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private Long phone;

    @NotBlank
    private String city;

    @OneToMany(mappedBy="citizen", cascade = CascadeType.ALL)
    private List<Animal> animals;

    public Citizen(String firstName, String lastName, Long phone, String city) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.city = city;
    }

    public Citizen() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank String lastName) {
        this.lastName = lastName;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public @NotBlank String getCity() {
        return city;
    }

    public void setCity(@NotBlank String city) {
        this.city = city;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }
}
