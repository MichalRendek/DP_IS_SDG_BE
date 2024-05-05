package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User getUserByUid(String userId);

    User getUserByNameAndPassword(String name, String password);

    User getUserByMail(String mail);
}
