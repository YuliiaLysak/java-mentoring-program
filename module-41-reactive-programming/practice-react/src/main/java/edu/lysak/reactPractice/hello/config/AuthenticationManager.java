package edu.lysak.reactPractice.hello.config;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtUtil jwtUtil;

    public AuthenticationManager(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        Optional<String> username = jwtUtil.extractUsername(authToken);
        if (username.isEmpty() || !jwtUtil.isValidToken(authToken)) {
            return Mono.empty();
        }
        List<SimpleGrantedAuthority> authorities = jwtUtil.getRoles(authToken);
        var authenticationToken = new UsernamePasswordAuthenticationToken(username.get(), null, authorities);
        return Mono.just(authenticationToken);
    }
}
