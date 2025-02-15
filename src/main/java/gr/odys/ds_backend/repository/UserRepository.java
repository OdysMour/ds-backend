package gr.odys.ds_backend.repository;
import gr.odys.ds_backend.entity.User;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Hidden
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userProfile up LEFT JOIN FETCH up.animals a LEFT JOIN FETCH a.healthChecks WHERE u.id = ?1")
    Optional<User> findByIdWithProfile(Long id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userProfile up LEFT JOIN FETCH up.citizen LEFT JOIN FETCH u.roles")
    List<User> findAllWithProfiles();

}