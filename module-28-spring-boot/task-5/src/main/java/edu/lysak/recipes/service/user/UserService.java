package edu.lysak.recipes.service.user;

import edu.lysak.recipes.model.user.AuthGroup;
import edu.lysak.recipes.model.user.BlockedUser;
import edu.lysak.recipes.model.user.User;
import edu.lysak.recipes.model.user.UserPrincipal;
import edu.lysak.recipes.repository.user.AuthGroupRepository;
import edu.lysak.recipes.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    public static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 5 * 60 * 1000; // 5 minutes

    private final UserRepository userRepository;
    private final AuthGroupRepository authGroupRepository;

    public UserService(UserRepository userRepository, AuthGroupRepository authGroupRepository) {
        super();
        this.userRepository = userRepository;
        this.authGroupRepository = authGroupRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (null == user) {
            throw new UsernameNotFoundException("Cannot find user with email: " + email);
        }
        List<AuthGroup> authGroups = authGroupRepository.findByEmail(email);
        return new UserPrincipal(user, authGroups);
    }


    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        userRepository.updateFailedAttempts(newFailAttempts, user.getEmail());
    }

    public void resetFailedAttempts(String email) {
        userRepository.updateFailedAttempts(0, email);
    }

    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();

        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);

            userRepository.save(user);

            return true;
        }

        return false;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<BlockedUser> getBlockedUsers() {
        List<User> blockedUsers = userRepository.findAllBlockedUsers();
        return blockedUsers.stream()
                .map(this::mapToBlockedUser)
                .collect(Collectors.toList());
    }

    private BlockedUser mapToBlockedUser(User user) {
        BlockedUser blockedUser = new BlockedUser();
        blockedUser.setEmail(user.getEmail());
        blockedUser.setLockTime(user.getLockTime());
        return blockedUser;
    }
}
