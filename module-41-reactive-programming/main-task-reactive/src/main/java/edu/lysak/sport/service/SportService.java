package edu.lysak.sport.service;

import edu.lysak.sport.domain.Sport;
import edu.lysak.sport.domain.dto.SportAttributes;
import edu.lysak.sport.domain.dto.SportDto;
import edu.lysak.sport.repository.SportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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

    public Mono<Sport> save(SportDto sportDto) {
        Sport entity = createEntity(sportDto);
        return sportRepository.findByName(entity.getName())
                .flatMap(result -> toError(entity.getName()))
                .switchIfEmpty(sportRepository.save(entity));
    }

    public Flux<Sport> getAllSports() {
        return sportRepository.findAll();
    }

    public Mono<Sport> getSportById(Long id) {
        return sportRepository.findById(id);
    }

    public Mono<Sport> updateSportById(Long id, SportDto sportDto) {
        return sportRepository.findById(id)
                .map(it -> updateSportFromDb(it, sportDto))
                .flatMap(sportRepository::save);
    }

    public Mono<Boolean> deleteSportById(Long id) {
            return sportRepository.deleteSportBySportId(id)
                    .map(deletedCount -> deletedCount > 0);
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

    private Sport createEntity(SportDto sportDto) {
        return Sport.builder()
                .decathlonId(sportDto.getId().intValue())
                .name(sportDto.getAttributes().getName())
                .description(sportDto.getAttributes().getDescription())
                .slug(sportDto.getAttributes().getSlug())
                .icon(sportDto.getAttributes().getIcon())
                .build();
    }

    private Sport updateSportFromDb(Sport sportFromDb, SportDto sportDto) {
        sportFromDb.setDecathlonId(sportDto.getId().intValue());
        sportFromDb.setName(sportDto.getAttributes().getName());
        sportFromDb.setDescription(sportDto.getAttributes().getDescription());
        sportFromDb.setSlug(sportDto.getAttributes().getSlug());
        sportFromDb.setIcon(sportDto.getAttributes().getIcon());
        return sportFromDb;
    }

}
