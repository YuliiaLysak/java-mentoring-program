package edu.lysak.sport.repository;

import edu.lysak.sport.domain.Sport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static edu.lysak.sport.TestUtil.getSport;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataR2dbcTest
@TestPropertySource(properties = "test.db.file-path=./tmp/test_sport_db")
class SportRepositoryTest {

    private static final String TEST_DB_FILE = "./tmp/test_sport_db.mv.db";

    @Autowired
    private SportRepository sportRepository;

    @BeforeAll
    static void beforeAll() throws IOException {
        removeTemporalDatabaseFile();
    }

    @AfterAll
    static void afterAll() throws IOException {
        removeTemporalDatabaseFile();
    }

    @Test
    void findByName() {
        Long id = saveRecord();
        sportRepository.findByName("name")
                .as(StepVerifier::create)
                .consumeNextWith(it -> {
                    assertEquals(id, it.getSportId());
                    assertEquals(1, it.getDecathlonId());
                    assertEquals("name", it.getName());
                    assertEquals("description", it.getDescription());
                    assertEquals("slug", it.getSlug());
                    assertEquals("icon", it.getIcon());
                })
                .verifyComplete();
    }

    @Test
    void deleteSportBySportId() {
        Long id = saveRecord();
        sportRepository.deleteSportBySportId(id)
                .as(StepVerifier::create)
                .expectNextMatches(it -> it > 0)
                .verifyComplete();
    }

    @Test
    public void saveSport_shouldSuccessfullySaveSport() {
        Sport sport = getSport(null, 1, "name", "description", "slug", "icon");
        Publisher<Sport> setup = sportRepository.deleteAll().then(sportRepository.save(sport));
        Mono<Sport> find = sportRepository.findByName("name");
        Publisher<Sport> composite = Mono
                .from(setup)
                .then(find);
        StepVerifier
                .create(composite)
                .consumeNextWith(sportFromDb -> {
                    assertNotNull(sportFromDb.getSportId());
                    assertEquals("name", sportFromDb.getName());
                    assertEquals("description", sportFromDb.getDescription());
                    assertEquals("slug", sportFromDb.getSlug());
                    assertEquals("icon", sportFromDb.getIcon());
                })
                .verifyComplete();
    }

    private Long saveRecord() {
        Sport sport = getSport(null, 1, "name", "description", "slug", "icon");
        return sportRepository.save(sport).block().getSportId();
    }

    private static void removeTemporalDatabaseFile() throws IOException {
        String canonicalPath = new File(".").getCanonicalPath();
        Path testDbPath = Path.of(canonicalPath, TEST_DB_FILE);
        if (Files.exists(testDbPath)) {
            Files.delete(testDbPath);
        }
    }
}
