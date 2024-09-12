package gr.odys.ds_backend.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
public class AnimalHealthCheck {

    @Id
    @GeneratedValue
    private Long id;

    @CreatedDate
    private Date created;

    @Column
    private String description;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "animal_id")
    private Animal animal;

    public AnimalHealthCheck() {
    }

    public AnimalHealthCheck(Date created, String description) {
        this.created = created;
        this.description = description;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
