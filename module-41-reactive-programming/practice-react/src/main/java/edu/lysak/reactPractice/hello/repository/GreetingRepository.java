package edu.lysak.reactPractice.hello.repository;

import edu.lysak.reactPractice.hello.domain.Greeting;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GreetingRepository extends ReactiveCrudRepository<Greeting, Long> {
}
