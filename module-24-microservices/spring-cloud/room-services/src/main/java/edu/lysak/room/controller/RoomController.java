package edu.lysak.room.controller;

import edu.lysak.room.domain.Room;
import edu.lysak.room.repository.RoomRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController {

    private final RoomRepository repository;

    public RoomController(RoomRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/rooms")
    public Iterable<Room> getAllRooms() {
        return repository.findAll();
    }

    @GetMapping("/rooms/{id}")
    public Room getRoom(@PathVariable("id") long id) {
        return repository.findById(id).get();
    }
}
