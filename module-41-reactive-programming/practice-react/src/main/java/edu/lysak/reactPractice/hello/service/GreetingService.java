package edu.lysak.reactPractice.hello.service;

import edu.lysak.reactPractice.hello.domain.Greeting;
import edu.lysak.reactPractice.hello.repository.GreetingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GreetingService {
    private final GreetingRepository greetingRepository;

    public GreetingService(GreetingRepository greetingRepository) {
        this.greetingRepository = greetingRepository;
    }

    public Flux<Greeting> list() {
        return greetingRepository.findAll();
    }

    public Mono<Greeting> addOne(Greeting greeting) {
        return greetingRepository.save(greeting);
    }
}
