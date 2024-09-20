package gr.odys.ds_backend.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gr.odys.ds_backend.entity.Animal;
import gr.odys.ds_backend.repository.AnimalRepository;

@RestController
@RequestMapping("/api/animals")
public class AnimalApi {

    @Autowired
    AnimalRepository animalRepository;

    @PostMapping
    public Animal createAnimal(@RequestBody Animal animal) {
        animal.setCitizen(null);
        return animalRepository.save(animal);
    }

    @GetMapping
    public List<Animal> getAnimals() {
        return animalRepository.findAll();
    }
}
