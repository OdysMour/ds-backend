package gr.odys.ds_backend.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@Entity
@Table(name = "animal_health_checks")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AnimalHealthCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    @JsonBackReference("animal-healthchecks")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AnimalHealthCheck{" +
                "id=" + id +
                ", created=" + created +
                ", description='" + description + '\'' +
                '}';
    }
}
