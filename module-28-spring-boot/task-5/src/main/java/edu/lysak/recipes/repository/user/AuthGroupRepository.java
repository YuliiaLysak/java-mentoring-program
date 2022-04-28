package edu.lysak.recipes.repository.user;

import edu.lysak.recipes.model.user.AuthGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthGroupRepository extends CrudRepository<AuthGroup, Long> {
    List<AuthGroup> findByEmail(String email);
}
