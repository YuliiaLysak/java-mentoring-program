package edu.lysak.guest.controller;

import edu.lysak.guest.domain.Guest;
import edu.lysak.guest.repository.GuestRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GuestController {

    private final GuestRepository repository;

    public GuestController(GuestRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/guests")
    public Iterable<Guest> getAllGuests() {
        return repository.findAll();
    }

    @GetMapping("/guests/{id}")
    public Guest getGuest(@PathVariable("id") long id) {
        return repository.findById(id).get();
    }
}
