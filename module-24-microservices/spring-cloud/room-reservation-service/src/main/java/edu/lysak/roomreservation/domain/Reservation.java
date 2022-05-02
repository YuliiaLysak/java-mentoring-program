package edu.lysak.roomreservation.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Reservation {
    private Long id;
    private Long roomId;
    private Long guestId;
    private String reservationDate;
}
