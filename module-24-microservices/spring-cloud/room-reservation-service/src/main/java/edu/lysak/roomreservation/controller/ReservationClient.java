package edu.lysak.roomreservation.controller;

import edu.lysak.roomreservation.domain.Reservation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@FeignClient("reservationservices")
public interface ReservationClient {
    @GetMapping("/reservations")
    List<Reservation> getAllReservations(@RequestParam(name = "date", required = false) String date);


    @GetMapping("/reservations/{id}")
    Reservation getReservation(@PathVariable("id") long id);
}
