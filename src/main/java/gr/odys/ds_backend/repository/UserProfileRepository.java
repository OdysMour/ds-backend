package gr.odys.ds_backend.repository;

import gr.odys.ds_backend.entity.UserProfile;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Hidden
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
