package gr.odys.ds_backend.repository;

import gr.odys.ds_backend.entity.Animal;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "animals")
//@Hidden
public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    
}
