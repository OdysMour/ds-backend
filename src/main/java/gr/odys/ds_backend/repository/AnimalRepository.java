package gr.odys.ds_backend.repository;

import gr.odys.ds_backend.entity.Animal;
import gr.odys.ds_backend.entity.UserProfile;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(path = "animals")
@Hidden // Hide from auto-generated REST API to use custom AnimalApi endpoints
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<Animal> findByUserProfile(UserProfile userProfile);
}
