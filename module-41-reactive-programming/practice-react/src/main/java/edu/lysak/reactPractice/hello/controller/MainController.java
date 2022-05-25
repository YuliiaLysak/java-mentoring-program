package edu.lysak.reactPractice.hello.controller;

import edu.lysak.reactPractice.hello.domain.Greeting;
import edu.lysak.reactPractice.hello.service.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/controller")
public class MainController {
    private final GreetingService greetingService;

    public MainController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping
    public Flux<Greeting> list(
            @RequestParam(defaultValue = "0") Long start,
            @RequestParam(defaultValue = "3") Long count
    ) {
        return greetingService.list();
    }

    @PostMapping
    public Mono<Greeting> addGreeting(@RequestBody Greeting greeting) {
        return greetingService.addOne(greeting);
    }
}
