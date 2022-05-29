package edu.lysak.sport.repository;

import edu.lysak.sport.domain.Sport;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SportRepository extends ReactiveCrudRepository<Sport, Long> {

    Mono<Sport> findByName(String name);

    Mono<Integer> deleteSportBySportId(Long sportId);
}
