package bo.edu.uagrm.ugram.identity.repository;

import bo.edu.uagrm.ugram.identity.entity.User;
import bo.edu.uagrm.ugram.identity.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByRu(String ru);

    Optional<User> findByEmailOrRu(String email, String ru);

    boolean existsByEmail(String email);

    boolean existsByRu(String ru);

    long countByUserType(UserType userType);
}
