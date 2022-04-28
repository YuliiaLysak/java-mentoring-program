package edu.lysak.recipes.repository.user;

import edu.lysak.recipes.model.user.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
}
