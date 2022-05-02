package edu.lysak.reservation.controller;

import edu.lysak.reservation.domain.Reservation;
import edu.lysak.reservation.repository.ReservationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

    private final ReservationRepository repository;

    public ReservationController(ReservationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/reservations")
    public Iterable<Reservation> getAllReservations(@RequestParam(name = "date", required = false) String date) {
        if (date == null || date.isEmpty() || date.isBlank()) {
            return repository.findAll();
        }
        return repository.findAllByReservationDate(date);
    }

    @GetMapping("/reservations/{id}")
    public Reservation getReservation(@PathVariable("id") long id) {
        return repository.findById(id).get();
    }
}
