package edu.lysak.sport.handler;

import edu.lysak.sport.domain.Sport;
import edu.lysak.sport.domain.dto.SportDto;
import edu.lysak.sport.service.SportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
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
                                .flatMap(this::successSavedResponse)
                );
    }

    @NonNull
    public Mono<ServerResponse> saveSportV3(ServerRequest request) {
        return sportService.saveSport(request.pathVariable("sportname"))
                .flatMap(this::successSavedResponse)
                .onErrorResume(this::badRequestFromError);
    }

    @NonNull
    public Mono<ServerResponse> getAllSports(ServerRequest request) {
        return sportService.getAllSports()
                .collectList()
                .flatMap(this::successResponseWithBody);
    }

    @NonNull
    public Mono<ServerResponse> getSportById(ServerRequest request) {
        if (invalidSportId(request.pathVariable("id"))) {
            return badRequestWithMessage("Sport id must be an integer");
        }
        return sportService.getSportById(Long.parseLong(request.pathVariable("id")))
                .flatMap(this::successResponseWithBody)
                .switchIfEmpty(notFoundResponse());
    }

    @NonNull
    public Mono<ServerResponse> getSportByName(ServerRequest request) {
        return sportService.findByName(request.pathVariable("name"))
                .flatMap(this::successResponseWithBody)
                .switchIfEmpty(notFoundResponse());
    }

    @NonNull
    public Mono<ServerResponse> saveSportWithBody(ServerRequest request) {
        return request.bodyToMono(SportDto.class)
                .flatMap(sportService::save)
                .flatMap(this::successSavedResponse)
                .onErrorResume(this::badRequestFromError);
    }

    @NonNull
    public Mono<ServerResponse> updateSportById(ServerRequest request) {
        String id = request.pathVariable("id");
        if (invalidSportId(id)) {
            return badRequestWithMessage("Sport id must be an integer");
        }

        return request.bodyToMono(SportDto.class)
                .flatMap(sportDto -> sportService.updateSportById(Long.parseLong(id), sportDto))
                .flatMap(this::successUpdatedResponse)
                .switchIfEmpty(badRequestWithMessage(
                        String.format("Cannot update. Sport with id=%s doesn't exist", id)
                ));
    }

    @NonNull
    public Mono<ServerResponse> deleteSportById(ServerRequest request) {
        String id = request.pathVariable("id");
        if (invalidSportId(id)) {
            return badRequestWithMessage("Sport id must be an integer");
        }

        return sportService.deleteSportById(Long.parseLong(id))
                .flatMap(deleted -> deleted
                        ? successResponseWithBody("Deleted")
                        : badRequestWithMessage(String.format("Sport with id='%s' doesn't exist", id))
                );
    }

    private Mono<Sport> persistNewEntity(String sportName) {
        return sportService.save(new Sport(sportName));
    }

    private Mono<ServerResponse> toBadRequestWhenExists(Sport sport) {
        return ServerResponse.badRequest()
                .bodyValue(String.format("Sport with name '%s' already exists", sport.getName()));
    }

    private Mono<ServerResponse> successResponseWithBody(Object object) {
        return ServerResponse.ok().bodyValue(object);
    }

    private Mono<ServerResponse> successSavedResponse(Sport sport) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(String.format(
                        "Saved new sport with id=%s, name='%s'",
                        sport.getSportId(),
                        sport.getName())
                );
    }

    private Mono<ServerResponse> successUpdatedResponse(Sport sport) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(String.format(
                        "Updated existing sport with id=%s, name='%s'",
                        sport.getSportId(),
                        sport.getName())
                );
    }

    private Mono<ServerResponse> badRequestFromError(Throwable error) {
        return ServerResponse.badRequest()
                .bodyValue(error.getMessage());
    }

    private Mono<ServerResponse> badRequestWithMessage(String message) {
        return ServerResponse.badRequest()
                .bodyValue(message);
    }

    private Mono<ServerResponse> notFoundResponse() {
        return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Sport not found");
    }

    private boolean invalidSportId(String id) {
        try {
            Long.parseLong(id);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
