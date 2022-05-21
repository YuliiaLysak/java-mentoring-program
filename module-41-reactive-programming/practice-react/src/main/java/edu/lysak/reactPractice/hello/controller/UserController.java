package edu.lysak.reactPractice.hello.controller;

import edu.lysak.reactPractice.hello.config.JwtUtil;
import edu.lysak.reactPractice.hello.domain.User;
import edu.lysak.reactPractice.hello.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
public class UserController {
    private static final ResponseEntity<Object> UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public UserController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity> login(ServerWebExchange serverWebExchange) {
        return serverWebExchange
                .getFormData()
                .flatMap(credentials ->
                        userService.findByUsername(credentials.getFirst("username"))
                                .cast(User.class)
                                .map(user ->
                                        Objects.equals(
                                                credentials.getFirst("password"),
                                                user.getPassword()
                                        )
                                                ? ResponseEntity.ok(jwtUtil.generateToken(user))
                                                : UNAUTHORIZED
                                )
                                .defaultIfEmpty(UNAUTHORIZED)
                );
    }
}
