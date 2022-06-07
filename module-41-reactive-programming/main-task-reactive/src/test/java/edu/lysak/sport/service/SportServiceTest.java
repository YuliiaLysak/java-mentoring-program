package edu.lysak.sport.service;

import edu.lysak.sport.domain.Sport;
import edu.lysak.sport.domain.dto.SportDto;
import edu.lysak.sport.repository.SportRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static edu.lysak.sport.TestUtil.getSport;
import static edu.lysak.sport.TestUtil.getSportDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SportServiceTest {

    private SportRepository sportRepository;

    private SportService sportService;

    @BeforeEach
    void setUp() {
        sportRepository = mock(SportRepository.class);
        sportService = new SportService(sportRepository);
    }

    @Test
    void deleteAll() {
        when(sportRepository.deleteAll()).thenReturn(Mono.empty());

        Mono<Void> voidMono = sportService.deleteAll();
        StepVerifier
                .create(voidMono)
                .verifyComplete();

        verify(sportRepository).deleteAll();
    }

    @Test
    void save_shouldSuccessfullySaveSport() {
        SportDto sportDto = getSportDto(1L, "name", "description", "slug", "icon");
        Sport sport = getSport(1L, 1, "name", "description", "slug", "icon");
        when(sportRepository.findByName(any())).thenReturn(Mono.empty());
        when(sportRepository.save(any())).thenReturn(Mono.just(sport));

        Mono<Sport> monoSport = sportService.save(sportDto);
        StepVerifier
                .create(monoSport)
                .consumeNextWith(savedSport -> {
                    assertNotNull(savedSport.getSportId());
                    assertEquals(1, savedSport.getDecathlonId());
                    assertEquals("name", savedSport.getName());
                    assertEquals("description", savedSport.getDescription());
                    assertEquals("slug", savedSport.getSlug());
                    assertEquals("icon", savedSport.getIcon());
                })
                .verifyComplete();

        verify(sportRepository).findByName("name");
        ArgumentCaptor<Sport> sportCaptor = ArgumentCaptor.forClass(Sport.class);
        verify(sportRepository).save(sportCaptor.capture());
        Sport savedSport = sportCaptor.getValue();
        assertEquals(1, savedSport.getDecathlonId());
        assertEquals("name", savedSport.getName());
        assertEquals("description", savedSport.getDescription());
        assertEquals("slug", savedSport.getSlug());
        assertEquals("icon", savedSport.getIcon());
    }

    @Test
    void save_shouldNotSaveSportIfAlreadyExistWithProvidedName() {
        SportDto sportDto = getSportDto(1L, "name", "description", "slug", "icon");
        Sport sport = getSport(1L, 1, "name", "description", "slug", "icon");
        when(sportRepository.findByName(any())).thenReturn(Mono.just(sport));
        when(sportRepository.save(any())).thenReturn(Mono.empty());

        Mono<Sport> monoSport = sportService.save(sportDto);
        StepVerifier
                .create(monoSport)
                .consumeErrorWith(error -> {
                    assertTrue(error instanceof IllegalArgumentException);
                    assertEquals("Sport with name 'name' already exists", error.getMessage());
                })
                .verify();

        verify(sportRepository).findByName("name");
        ArgumentCaptor<Sport> sportCaptor = ArgumentCaptor.forClass(Sport.class);
        verify(sportRepository).save(sportCaptor.capture());
        Sport updatedSport = sportCaptor.getValue();
        assertEquals(1, updatedSport.getDecathlonId());
        assertEquals("name", updatedSport.getName());
        assertEquals("description", updatedSport.getDescription());
        assertEquals("slug", updatedSport.getSlug());
        assertEquals("icon", updatedSport.getIcon());
    }

    @Test
    void getAllSports() {
        Sport sport1 = getSport(1L, 1, "name", "description", "slug", "icon");
        Sport sport2 = getSport(2L, 2, "name2", "description2", "slug2", "icon2");

        when(sportRepository.findAll()).thenReturn(Flux.just(sport1, sport2));

        Flux<Sport> allSports = sportService.getAllSports();
        StepVerifier
                .create(allSports)
                .expectNextCount(2)
                .verifyComplete();

        verify(sportRepository).findAll();
    }

    @Test
    void getSportById_shouldSuccessfullyFindSportById() {
        Sport sport = getSport(1L, 1, "name", "description", "slug", "icon");
        when(sportRepository.findById(anyLong())).thenReturn(Mono.just(sport));

        Mono<Sport> sportMono = sportService.getSportById(1L);
        StepVerifier
                .create(sportMono)
                .consumeNextWith(sportFromDb -> assertEquals(1L, sportFromDb.getSportId()))
                .verifyComplete();

        verify(sportRepository).findById(1L);
    }

    @Test
    void getSportById_shouldNotFindSportById() {
        when(sportRepository.findById(anyLong())).thenReturn(Mono.empty());

        Mono<Sport> sportMono = sportService.getSportById(1L);
        StepVerifier
                .create(sportMono)
                .expectNextCount(0)
                .verifyComplete();

        verify(sportRepository).findById(1L);
    }

    @Test
    void findByName_shouldSuccessfullyFindSportByName() {
        Sport sport = getSport(1L, 1, "name", "description", "slug", "icon");
        when(sportRepository.findByName(any())).thenReturn(Mono.just(sport));

        Mono<Sport> sportMono = sportService.findByName("name");
        StepVerifier
                .create(sportMono)
                .consumeNextWith(sportFromDb -> assertEquals("name", sportFromDb.getName()))
                .verifyComplete();

        verify(sportRepository).findByName("name");
    }

    @Test
    void findByName_shouldNotFindSportByName() {
        when(sportRepository.findByName(any())).thenReturn(Mono.empty());

        Mono<Sport> sportMono = sportService.findByName("name");
        StepVerifier
                .create(sportMono)
                .expectNextCount(0)
                .verifyComplete();

        verify(sportRepository).findByName("name");
    }

    @Test
    void updateSportById_shouldSuccessfullyUpdateSport() {
        Sport sport = getSport(1L, 1, "name", "description", "slug", "icon");
        SportDto sportDto = getSportDto(1L, "updated name", "updated description", "updated slug", "updated icon");
        Sport updSport = getSport(1L, 1, "updated name", "updated description", "updated slug", "updated icon");
        when(sportRepository.findById(anyLong())).thenReturn(Mono.just(sport));
        when(sportRepository.save(any())).thenReturn(Mono.just(updSport));

        Mono<Sport> sportMono = sportService.updateSportById(1L, sportDto);
        StepVerifier
                .create(sportMono)
                .consumeNextWith(updatedSport -> {
                    assertEquals(1L, updatedSport.getSportId());
                    assertEquals(1, updatedSport.getDecathlonId());
                    assertEquals("updated name", updatedSport.getName());
                    assertEquals("updated description", updatedSport.getDescription());
                    assertEquals("updated slug", updatedSport.getSlug());
                    assertEquals("updated icon", updatedSport.getIcon());
                })
                .verifyComplete();

        verify(sportRepository).findById(1L);
        ArgumentCaptor<Sport> sportCaptor = ArgumentCaptor.forClass(Sport.class);
        verify(sportRepository).save(sportCaptor.capture());
        Sport updatedSport = sportCaptor.getValue();
        assertEquals(1L, updatedSport.getSportId());
        assertEquals(1, updatedSport.getDecathlonId());
        assertEquals("updated name", updatedSport.getName());
        assertEquals("updated description", updatedSport.getDescription());
        assertEquals("updated slug", updatedSport.getSlug());
        assertEquals("updated icon", updatedSport.getIcon());
    }

    @Test
    void updateSportById_shouldNotUpdateAnythingIfSportNotFound() {
        when(sportRepository.findById(anyLong())).thenReturn(Mono.empty());

        Mono<Sport> sportMono = sportService.updateSportById(1L, new SportDto());
        StepVerifier
                .create(sportMono)
                .expectNextCount(0)
                .verifyComplete();

        verify(sportRepository).findById(1L);
        verify(sportRepository, never()).save(any());
    }

    @Test
    void deleteSportById_shouldSuccessfullyDeleteSport() {
        when(sportRepository.deleteSportBySportId(anyLong())).thenReturn(Mono.just(1));

        Mono<Boolean> deleted = sportService.deleteSportById(1L);
        StepVerifier
                .create(deleted)
                .consumeNextWith(Assertions::assertTrue)
                .verifyComplete();

        verify(sportRepository).deleteSportBySportId(1L);
    }

    @Test
    void deleteSportById_shouldNotDeleteSport() {
        when(sportRepository.deleteSportBySportId(anyLong())).thenReturn(Mono.just(0));

        Mono<Boolean> deleted = sportService.deleteSportById(1L);
        StepVerifier
                .create(deleted)
                .consumeNextWith(Assertions::assertFalse)
                .verifyComplete();

        verify(sportRepository).deleteSportBySportId(1L);
    }
}
