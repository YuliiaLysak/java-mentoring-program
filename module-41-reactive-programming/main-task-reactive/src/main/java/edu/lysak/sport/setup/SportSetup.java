package edu.lysak.sport.setup;

import edu.lysak.sport.domain.Sport;
import edu.lysak.sport.domain.dto.SportData;
import edu.lysak.sport.service.SportService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SportSetup {
    private final WebClient client;
    private final SportService sportService;

    public SportSetup(WebClient client, SportService sportService) {
        this.client = client;
        this.sportService = sportService;
    }

    public void getSportDataAndSaveToDb() {
        client
                .get()
                .retrieve()
                .bodyToMono(SportData.class)
                .subscribe(it -> it.getData()
                        .stream()
                        .map(sportDto -> {
                            Sport sport = new Sport();
                            sport.setDecathlonId(sportDto.getId());
                            sport.setName(sportDto.getAttributes().getName());
                            sport.setDescription(sportDto.getAttributes().getDescription());
                            sport.setSlug(sportDto.getAttributes().getSlug());
                            sport.setIcon(sportDto.getAttributes().getIcon());
                            return sport;
                        })
                        .forEach(record -> sportService.save(record).subscribe()));
    }
}
