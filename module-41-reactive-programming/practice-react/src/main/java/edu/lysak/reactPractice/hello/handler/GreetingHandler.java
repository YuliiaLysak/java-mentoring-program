package edu.lysak.reactPractice.hello.handler;

import edu.lysak.reactPractice.hello.domain.Greeting;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class GreetingHandler {

    public Mono<ServerResponse> helloSimple(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new Greeting("Hello, Spring!")));
    }

    public Mono<ServerResponse> hello(ServerRequest request) {
        Long start = request.queryParam("start")
                .map(Long::valueOf)
                .orElse(0L);
        Long count = request.queryParam("count")
                .map(Long::valueOf)
                .orElse(3L);

        Flux<Greeting> data = Flux
                .just(
                        "Hello, reactive!",
                        "More then one",
                        "Third post",
                        "Fourth post",
                        "Fifth post"
                )
                .skip(start)
                .take(count)
                .map(Greeting::new);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(data, Greeting.class);
    }

    public Mono<ServerResponse> index(ServerRequest serverRequest) {
        String user = serverRequest.queryParam("user")
                .orElse("Nobody");

        return ServerResponse
                .ok()
                .render("index", Map.of("user", user));
    }
}
