package bo.edu.uagrm.ugram.identity.repository;

import bo.edu.uagrm.ugram.identity.entity.User;
import bo.edu.uagrm.ugram.identity.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByCi(String ci);

    Optional<User> findByEmailOrCi(String email, String ci);

    Optional<User> findByIdAndUserTypeIn(UUID id, Collection<UserType> userTypes);

    List<User> findByUserTypeInOrderByCreatedAtDesc(Collection<UserType> userTypes);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByCi(String ci);

    boolean existsByCiAndIdNot(String ci, UUID id);

    long countByUserType(UserType userType);
}
