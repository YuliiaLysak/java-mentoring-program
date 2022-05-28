package edu.lysak.sport.service;

import edu.lysak.sport.domain.Sport;
import edu.lysak.sport.domain.dto.SportAttributes;
import edu.lysak.sport.domain.dto.SportDto;
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

    public Mono<Void> deleteAll() {
        return sportRepository.deleteAll();
    }

    public Mono<Sport> save(Sport sport) {
        return sportRepository.save(sport);
    }

    public Mono<SportDto> findByNameAsDto(String sportName) {
        Mono<Sport> sport = sportRepository.findByName(sportName);
        return sport.map(this::convertToDto);
    }

    public Mono<Sport> findByName(String sportName) {
        return sportRepository.findByName(sportName);
    }

    public Mono<Sport> saveSport(String sportName) {
        Sport entity = new Sport(sportName);
        return sportRepository.findByName(sportName)
                .flatMap(result -> toError(sportName))
                .switchIfEmpty(sportRepository.save(entity));
    }

    private SportDto convertToDto(Sport sport) {
        return SportDto.builder()
                .id(sport.getSportId())
                .attributes(SportAttributes.builder()
                        .name(sport.getName())
                        .description(sport.getDescription())
                        .slug(sport.getSlug())
                        .icon(sport.getIcon())
                        .build())
                .build();
    }

    private Mono<Sport> toError(String sportName) {
        return Mono.error(new IllegalArgumentException(
                String.format("Sport with name '%s' already exists", sportName))
        );
    }
}
