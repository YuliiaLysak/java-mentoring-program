package edu.lysak.test.controller;

import edu.lysak.test.domain.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationResourceTest {

    @Autowired
    private ApplicationContext applicationContext;
    private WebTestClient webTestClient;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient
                .bindToApplicationContext(applicationContext)
                .build();

        reservation = new Reservation(
                125L,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                150
        );
    }

    @Test
    void getAllReservations() {
        webTestClient
                .get()
                .uri("/room/v1/reservation")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Reservation.class);
    }

    @Test
    void createReservation() {
        webTestClient
                .post()
                .uri("/room/v1/reservation")
                .body(Mono.just(reservation), Reservation.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Reservation.class);
    }
}