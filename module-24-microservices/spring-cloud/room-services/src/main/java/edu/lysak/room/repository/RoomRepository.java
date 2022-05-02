package edu.lysak.room.repository;

import edu.lysak.room.domain.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
}
