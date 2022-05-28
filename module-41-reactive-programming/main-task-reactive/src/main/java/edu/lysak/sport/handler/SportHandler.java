package edu.lysak.sport.handler;

import edu.lysak.sport.domain.Sport;
import edu.lysak.sport.service.SportService;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class SportHandler {
    private final SportService sportService;

    public SportHandler(SportService sportService) {
        this.sportService = sportService;
    }

    @NonNull
    public Mono<ServerResponse> getSport(ServerRequest request) {
        Optional<String> queryParam = request.queryParam("sportname");
        if (queryParam.isEmpty()) {
            return ServerResponse
                    .badRequest()
                    .bodyValue("Required queryParam is missing");
        }

        return sportService.findByNameAsDto(queryParam.get())
                .flatMap(it ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(it)
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @NonNull
    public Mono<ServerResponse> saveSportV1(ServerRequest request) {
        String sportname = request.pathVariable("sportname");

        return sportService.findByName(sportname)
                .flatMap(it ->
                        ServerResponse.badRequest()
                                .bodyValue(String.format("Sport with name '%s' already exists", sportname))
                )
                .switchIfEmpty(
                        sportService.save(new Sport(sportname))
                                .flatMap(it ->
                                        ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue("SAVED")
                                )
                );

    }

    @NonNull
    public Mono<ServerResponse> handleAddingSportEntity(ServerRequest request) {
        String sportname = request.pathVariable("sportname");

        return sportService.findByName(sportname)
                .flatMap(this::toBadRequestWhenExists)
                .switchIfEmpty(
                        persistNewEntity(sportname)
                                .flatMap(this::successResponse)
                );
    }

    @NonNull
    public Mono<ServerResponse> saveSportV3(ServerRequest request) {
        return sportService.saveSport(request.pathVariable("sportname"))
                .flatMap(this::successResponse)
                .onErrorResume(this::badRequest);
    }

    private Mono<Sport> persistNewEntity(String sportName) {
        return sportService.save(new Sport(sportName));
    }

    private Mono<ServerResponse> toBadRequestWhenExists(Sport sport) {
        return ServerResponse.badRequest()
                .bodyValue(String.format("Sport with name '%s' already exists", sport.getName()));
    }

    private Mono<ServerResponse> successResponse(Sport sport) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(String.format("Saved new sport with name '%s'", sport.getName()));
    }

    private Mono<ServerResponse> badRequest(Throwable error) {
        return ServerResponse.badRequest()
                .bodyValue(error.getMessage());
    }
}
