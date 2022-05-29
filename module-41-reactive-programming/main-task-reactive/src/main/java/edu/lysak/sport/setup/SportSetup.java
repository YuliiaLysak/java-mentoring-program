package edu.lysak.sport.setup;

import edu.lysak.sport.domain.Sport;
import edu.lysak.sport.domain.dto.SportData;
import edu.lysak.sport.domain.dto.SportDto;
import edu.lysak.sport.service.SportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class SportSetup {
    private final WebClient client;
    private final SportService sportService;
    private final SubscriberWithBackpressure sportSubscriber;

    public SportSetup(
            WebClient client,
            SportService sportService,
            SubscriberWithBackpressure sportSubscriber
    ) {
        this.client = client;
        this.sportService = sportService;
        this.sportSubscriber = sportSubscriber;
    }

    // First Attempt - don't need to use anymore
    public void getSportDataAndSaveToDb() {
        client
                .get()
                .retrieve()
                .bodyToMono(SportData.class)
                .subscribe(it -> it.getData()
                        .stream()
                        .map(this::createSport)
                        .forEach(record -> sportService.save(record).subscribe())
                );
    }

    public void initSportsWithBackPressure() {
        sportService.deleteAll().subscribe();
        log.info("Deleted data in db before initialization");

        client
                .get()
                .retrieve()
                .bodyToMono(SportData.class)
                .flatMapMany(sportData -> Flux.fromIterable(sportData.getData()))
                .map(this::createSport)
                .log()
                .subscribe(sportSubscriber);
    }

    private Sport createSport(SportDto sportDto) {
        return Sport.builder()
                .decathlonId(sportDto.getId().intValue())
                .name(sportDto.getAttributes().getName())
                .description(sportDto.getAttributes().getDescription())
                .slug(sportDto.getAttributes().getSlug())
                .icon(sportDto.getAttributes().getIcon())
                .build();
    }

}
