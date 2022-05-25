package edu.lysak.test.controller;

import edu.lysak.test.domain.Reservation;
import edu.lysak.test.service.ReservationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin // because Angular and Spring Boot app runs on different ports (DON'T USE it in prod)
public class ReservationResource {
    private final ReservationService reservationService;

    public ReservationResource(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping(value = "/room/v1/reservation/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Reservation> getReservationById(@PathVariable String id) {
        return reservationService.getReservation(id);
    }

    @GetMapping(value = "/room/v1/reservation", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Reservation> getAllReservations() {
        return reservationService.listAllReservations();
    }

    @PostMapping(value = "/room/v1/reservation",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Reservation> createReservation(@RequestBody Mono<Reservation> reservation) {
        return reservationService.createReservation(reservation);
    }

    @PutMapping(value = "/room/v1/reservation/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Reservation> updatePrice(@PathVariable String id, @RequestBody Mono<Reservation> reservation) {
        return reservationService.updateReservation(id, reservation);
    }

    @DeleteMapping("/room/v1/reservation/{id}")
    public Mono<Boolean> deleteReservation(@PathVariable String id) {
        return reservationService.deleteReservation(id);
    }
}
