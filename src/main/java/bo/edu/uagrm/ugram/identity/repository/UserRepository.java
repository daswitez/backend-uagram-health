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

    Optional<User> findByCi(String ci);

    Optional<User> findByEmailOrCi(String email, String ci);

    boolean existsByEmail(String email);

    boolean existsByCi(String ci);

    long countByUserType(UserType userType);
}
