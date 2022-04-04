package edu.lysak.events.repository;

import edu.lysak.events.domain.Event;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Event e WHERE e.id=:id")
    int delete(@Param("id") Long id);

    Iterable<Event> findAllByTitle(String title);

    Optional<Event> findByTitleAndSpeakerAndDateTime(String title, String speaker, LocalDateTime dateTime);
}
