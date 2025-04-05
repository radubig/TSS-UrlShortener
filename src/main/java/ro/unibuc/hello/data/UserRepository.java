package ro.unibuc.hello.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {
    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
