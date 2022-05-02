package edu.lysak.reservation.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @Column(name = "reservation_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "guest_id")
    private Long guestId;

    @Column(name = "res_date")
    private String reservationDate;
}
