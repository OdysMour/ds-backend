package gr.odys.ds_backend.repository;

import gr.odys.ds_backend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RepositoryRestResource(path = "user-profiles")
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile findByUserId(Long userId);
    
    @Query("SELECT up FROM UserProfile up LEFT JOIN FETCH up.animals WHERE up.id = ?1")
    Optional<UserProfile> findByIdWithAnimals(Long id);
}
