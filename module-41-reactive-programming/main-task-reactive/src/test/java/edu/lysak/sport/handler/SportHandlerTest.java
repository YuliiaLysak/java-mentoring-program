package edu.lysak.sport.handler;

import edu.lysak.sport.config.SportRouter;
import edu.lysak.sport.domain.Sport;
import edu.lysak.sport.domain.dto.SportDto;
import edu.lysak.sport.service.SportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static edu.lysak.sport.TestUtil.getSport;
import static edu.lysak.sport.TestUtil.getSportDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {SportRouter.class, SportHandler.class})
@WebFluxTest
class SportHandlerTest {

    @Autowired
    private ApplicationContext context;

    @MockBean
    private SportService sportService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient
                .bindToApplicationContext(context)
                .build();
    }

    @Nested
    @DisplayName("#getAllSports()")
    class GetAllSportsMethodTest {

        @Test
        @DisplayName("should return list of all sports and status 200 (ok)")
        void getAllSports() {
            Sport sport1 = getSport(1L, 1, "name", "description", "slug", "icon");
            Sport sport2 = getSport(2L, 2, "name2", "description2", "slug2", "icon2");

            when(sportService.getAllSports()).thenReturn(Flux.just(sport1, sport2));

            webTestClient.get()
                    .uri("/handler/sports")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Sport.class)
                    .value(sportResponse -> {
                                Sport receivedSport1 = sportResponse.get(0);
                                assertEquals(1L, receivedSport1.getSportId());
                                assertEquals(1, receivedSport1.getDecathlonId());
                                assertEquals("name", receivedSport1.getName());
                                assertEquals("description", receivedSport1.getDescription());
                                assertEquals("slug", receivedSport1.getSlug());
                                assertEquals("icon", receivedSport1.getIcon());

                                Sport receivedSport2 = sportResponse.get(1);
                                assertEquals(2L, receivedSport2.getSportId());
                                assertEquals(2, receivedSport2.getDecathlonId());
                                assertEquals("name2", receivedSport2.getName());
                                assertEquals("description2", receivedSport2.getDescription());
                                assertEquals("slug2", receivedSport2.getSlug());
                                assertEquals("icon2", receivedSport2.getIcon());
                            }
                    );

            verify(sportService).getAllSports();
        }

        @Test
        @DisplayName("should return empty list if no sport exist and status 200 (ok)")
        void getAllSports_shouldReturnEmptyList() {
            when(sportService.getAllSports()).thenReturn(Flux.empty());

            webTestClient.get()
                    .uri("/handler/sports")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Sport.class)
                    .value(sportResponse -> assertTrue(sportResponse.isEmpty()));

            verify(sportService).getAllSports();
        }
    }

    @Nested
    @DisplayName("#getSportById()")
    class GetSportByIdMethodTest {

        @Test
        @DisplayName("should return found sport and status 200 (ok)")
        void getSportById() {
            Sport sport = getSport(1L, 1, "name", "description", "slug", "icon");
            Mono<Sport> sportMono = Mono.just(sport);
            when(sportService.getSportById(any())).thenReturn(sportMono);

            webTestClient.get()
                    .uri("/handler/sports/{id}", 1)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Sport.class)
                    .value(sportResponse -> {
                                assertEquals(1L, sportResponse.getSportId());
                                assertEquals(1, sportResponse.getDecathlonId());
                                assertEquals("name", sportResponse.getName());
                                assertEquals("description", sportResponse.getDescription());
                                assertEquals("slug", sportResponse.getSlug());
                                assertEquals("icon", sportResponse.getIcon());
                            }
                    );

            verify(sportService).getSportById(1L);
        }

        @Test
        @DisplayName("should not return any sport and status 404 (not found) if sport does not exist")
        void getSportById_shouldNotReturnAnySport() {
            when(sportService.getSportById(any())).thenReturn(Mono.empty());

            webTestClient.get()
                    .uri("/handler/sports/{id}", 1)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(String.class)
                    .value(response -> assertEquals("Sport not found", response));

            verify(sportService).getSportById(1L);
        }

        @Test
        @DisplayName("should return error message and status 400 (bad request) if path variable 'id' is invalid")
        void getSportById_shouldReturnErrorMessage_ifPathVariableInvalid() {
            webTestClient.get()
                    .uri("/handler/sports/{id}", "invalid")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .value(response -> assertEquals("Sport id must be an integer", response));

            verify(sportService, never()).getSportById(any());
        }
    }

    @Nested
    @DisplayName("#getSportByName()")
    class GetSportByNameMethodTest {

        @Test
        @DisplayName("should return found sport and status 200 (ok)")
        void getSportByName() {
            Sport sport = getSport(1L, 1, "name", "description", "slug", "icon");
            Mono<Sport> sportMono = Mono.just(sport);
            when(sportService.findByName(any())).thenReturn(sportMono);

            webTestClient.get()
                    .uri("/handler/sports/name/{name}", "name")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Sport.class)
                    .value(sportResponse -> {
                                assertEquals(1L, sportResponse.getSportId());
                                assertEquals(1, sportResponse.getDecathlonId());
                                assertEquals("name", sportResponse.getName());
                                assertEquals("description", sportResponse.getDescription());
                                assertEquals("slug", sportResponse.getSlug());
                                assertEquals("icon", sportResponse.getIcon());
                            }
                    );

            verify(sportService).findByName("name");
        }

        @Test
        @DisplayName("should not return any sport and status 404 (not found) if sport does not exist")
        void getSportByName_shouldNotReturnAnySport() {
            when(sportService.findByName(any())).thenReturn(Mono.empty());

            webTestClient.get()
                    .uri("/handler/sports/name/{name}", "name")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(String.class)
                    .value(response -> assertEquals("Sport not found", response));

            verify(sportService).findByName("name");
        }
    }

    @Nested
    @DisplayName("#saveSportWithBody()")
    class SaveSportWithBodyMethodTest {

        @Test
        @DisplayName("should successfully save sport and return status 200 (ok)")
        void saveSportWithBody() {
            SportDto sportDto = getSportDto(1L, "name", "description", "slug", "icon");
            Sport sport = getSport(1L, 1, "name", "description", "slug", "icon");
            Mono<Sport> sportMono = Mono.just(sport);
            when(sportService.save(any(SportDto.class))).thenReturn(sportMono);

            webTestClient.post()
                    .uri("/handler/sports")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(sportDto), SportDto.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .value(response -> {
                                assertEquals("Saved new sport with id=1, name='name'", response);
                            }
                    );

            verify(sportService).save(sportDto);
        }

        @Test
        @DisplayName("should not save sport and return status 400 (bad request) if sport with provided name already exists")
        void saveSportWithBody_shouldNotSaveSport_ifSportWithProvidedNameAlreadyExist() {
            SportDto sportDto = getSportDto(1L, "name", "description", "slug", "icon");
            Mono<Sport> error = Mono.error(new IllegalArgumentException("Sport with name 'name' already exists"));
            when(sportService.save(any(SportDto.class))).thenReturn(error);

            webTestClient.post()
                    .uri("/handler/sports")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(sportDto), SportDto.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .value(response -> {
                                assertEquals("Sport with name 'name' already exists", response);
                            }
                    );

            verify(sportService).save(sportDto);
        }
    }

    @Nested
    @DisplayName("#updateSportById()")
    class UpdateSportByIdMethodTest {

        @Test
        @DisplayName("should successfully update sport and return status 200 (ok)")
        void updateSportById() {
            SportDto sportDto = getSportDto(1L, "updated name", "updated description", "updated slug", "updated icon");
            Sport sport = getSport(1L, 1, "updated name", "updated description", "updated slug", "updated icon");
            Mono<Sport> sportMono = Mono.just(sport);
            when(sportService.updateSportById(any(), any())).thenReturn(sportMono);

            webTestClient.put()
                    .uri("/handler/sports/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(sportDto), SportDto.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .value(response -> {
                                assertEquals("Updated existing sport with id=1, name='updated name'", response);
                            }
                    );

            verify(sportService).updateSportById(1L, sportDto);
        }

        @Test
        @DisplayName("should not update any sport and return status 400 (bad request) if sport not found")
        void updateSportById_shouldNotUpdateAnySport_ifItNotFound() {
            SportDto sportDto = getSportDto(1L, "updated name", "updated description", "updated slug", "updated icon");
            when(sportService.updateSportById(any(), any())).thenReturn(Mono.empty());

            webTestClient.put()
                    .uri("/handler/sports/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(sportDto), SportDto.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .value(response -> assertEquals("Cannot update. Sport with id=1 doesn't exist", response)
                    );

            verify(sportService).updateSportById(1L, sportDto);
        }

        @Test
        @DisplayName("should return error message and status 400 (bad request) if path variable 'id' is invalid")
        void updateSportById_shouldReturnError_ifPathVariableInvalid() {
            SportDto sportDto = getSportDto(1L, "updated name", "updated description", "updated slug", "updated icon");

            webTestClient.put()
                    .uri("/handler/sports/{id}", "invalid")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(sportDto), SportDto.class)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .value(response -> assertEquals("Sport id must be an integer", response)
                    );

            verify(sportService, never()).updateSportById(any(), any());
        }
    }

    @Nested
    @DisplayName("#deleteSportById()")
    class DeleteSportByIdMethodTest {

        @Test
        @DisplayName("should successfully delete sport and return status 200 (ok)")
        void deleteSportById() {

            Mono<Boolean> deleted = Mono.just(true);
            when(sportService.deleteSportById(any())).thenReturn(deleted);

            webTestClient.delete()
                    .uri("/handler/sports/{id}", 1)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .value(response -> {
                                assertEquals("Deleted", response);
                            }
                    );

            verify(sportService).deleteSportById(1L);
        }

        @Test
        @DisplayName("should not delete any sport and return status 400 (bad request) if sport for deletion does not exist")
        void deleteSportById_shouldNotDeleteAnySport_ifSportDoesNotExist() {

            Mono<Boolean> deleted = Mono.just(false);
            when(sportService.deleteSportById(any())).thenReturn(deleted);

            webTestClient.delete()
                    .uri("/handler/sports/{id}", 1)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .value(response -> assertEquals("Sport with id='1' doesn't exist", response));

            verify(sportService).deleteSportById(1L);
        }

        @Test
        @DisplayName("should return error message and status 400 (bad request) if path variable 'id' is invalid")
        void deleteSportById_shouldReturnErrorMessage_idPathVariableInvalid() {

            webTestClient.delete()
                    .uri("/handler/sports/{id}", "hjsdhd")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .value(response -> assertEquals("Sport id must be an integer", response));

            verify(sportService, never()).deleteSportById(any());
        }
    }
}
