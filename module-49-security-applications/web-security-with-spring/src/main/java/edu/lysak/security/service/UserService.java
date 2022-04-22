package edu.lysak.security.service;

import edu.lysak.security.domain.AuthGroup;
import edu.lysak.security.domain.User;
import edu.lysak.security.domain.UserPrincipal;
import edu.lysak.security.repository.AuthGroupRepository;
import edu.lysak.security.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

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
}
