package edu.lysak.sport.service;

import edu.lysak.sport.domain.Sport;
import edu.lysak.sport.repository.SportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SportService {
    private final SportRepository sportRepository;

    public SportService(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    public Mono<Sport> save(Sport sport) {
        log.info("Saving record to db, id={}", sport.getDecathlonId());
        return sportRepository.save(sport);
    }
}
