package edu.lysak.roomreservation.controller;

import edu.lysak.roomreservation.domain.Guest;
import edu.lysak.roomreservation.domain.Reservation;
import edu.lysak.roomreservation.domain.Room;
import edu.lysak.roomreservation.domain.RoomReservation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RoomReservationController {

    private final RoomClient roomClient;
    private final GuestClient guestClient;
    private final ReservationClient reservationClient;

    public RoomReservationController(
            RoomClient roomClient,
            GuestClient guestClient,
            ReservationClient reservationClient
    ) {
        this.roomClient = roomClient;
        this.guestClient = guestClient;
        this.reservationClient = reservationClient;
    }

    @GetMapping("/room-reservations")
    public List<RoomReservation> getRoomReservations(@RequestParam(name = "date", required = false) String date) {
        List<Room> rooms = roomClient.getAllRooms();

        Map<Long, RoomReservation> roomReservations = rooms.stream()
                .map(this::mapToRoomReservation)
                .collect(Collectors.toMap(RoomReservation::getRoomId, it -> it));
        List<Reservation> reservations = reservationClient.getAllReservations(date);
        reservations.forEach(reservation -> {
            RoomReservation roomReservation = roomReservations.get(reservation.getRoomId());
            Guest guest = guestClient.getGuest(reservation.getGuestId());
            roomReservation.setDate(date);
            roomReservation.setGuestId(guest.getId());
            roomReservation.setFirstName(guest.getFirstName());
            roomReservation.setLastName(guest.getLastName());
        });

        return new ArrayList<>(roomReservations.values());
    }

    private RoomReservation mapToRoomReservation(Room room) {
        RoomReservation roomReservation = new RoomReservation();
        roomReservation.setRoomNumber(room.getRoomNumber());
        roomReservation.setRoomName(room.getName());
        roomReservation.setRoomId(room.getId());
        return roomReservation;
    }
}

