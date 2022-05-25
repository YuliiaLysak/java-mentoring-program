package edu.lysak.sport.repository;

import edu.lysak.sport.domain.Sport;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportRepository extends ReactiveCrudRepository<Sport, Long> {
}
