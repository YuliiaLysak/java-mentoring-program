package edu.lysak.reservation.repository;

import edu.lysak.reservation.domain.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    Iterable<Reservation> findAllByReservationDate(String date);
}
