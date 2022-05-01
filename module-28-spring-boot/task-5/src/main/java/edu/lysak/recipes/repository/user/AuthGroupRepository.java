package edu.lysak.recipes.repository.user;

import edu.lysak.recipes.model.user.AuthGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthGroupRepository extends CrudRepository<AuthGroup, Long> {

    @Query("SELECT ag FROM AuthGroup ag WHERE ag.email = :email")
    List<AuthGroup> findByEmail(@Param("email") String email);
}
