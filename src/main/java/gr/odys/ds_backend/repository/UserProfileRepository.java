package gr.odys.ds_backend.repository;

import gr.odys.ds_backend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "user-profiles")
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
