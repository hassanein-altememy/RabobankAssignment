package nl.rabobank.mongo.repository;

import nl.rabobank.mongo.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user data by his name.
     *
     * @param name the name of the user
     * @return Optional containing user data if found.
     */
    Optional<User> findByName(String name);
}
