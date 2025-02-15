package gr.odys.ds_backend.repository;

import gr.odys.ds_backend.entity.Animal;
import gr.odys.ds_backend.entity.AnimalHealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalHealthCheckRepository extends JpaRepository<AnimalHealthCheck, Long> {
    List<AnimalHealthCheck> findByAnimal(Animal animal);
}
