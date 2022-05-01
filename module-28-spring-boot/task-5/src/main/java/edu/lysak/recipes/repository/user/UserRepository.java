package edu.lysak.recipes.repository.user;

import edu.lysak.recipes.model.user.BlockedUser;
import edu.lysak.recipes.model.user.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.email = ?2")
    void updateFailedAttempts(int failAttempts, String email);

    @Query("SELECT new edu.lysak.recipes.model.user.BlockedUser(u.email, u.lockTime) FROM User u WHERE u.accountNonLocked = false")
    List<BlockedUser> findAllBlockedUsers();
}
