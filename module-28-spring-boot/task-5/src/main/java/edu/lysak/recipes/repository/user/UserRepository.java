package edu.lysak.recipes.repository.user;

import edu.lysak.recipes.model.user.BlockedUser;
import edu.lysak.recipes.model.user.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.failedAttempt = :failAttempts WHERE u.email = :email")
    void updateFailedAttempts(@Param("failAttempts") int failAttempts, @Param("email") String email);

    @Query("SELECT new edu.lysak.recipes.model.user.BlockedUser(u.email, u.lockTime) FROM User u WHERE u.accountNonLocked = false")
    List<BlockedUser> findAllBlockedUsers();
}
