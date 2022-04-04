package edu.lysak.events;

import edu.lysak.events.domain.Event;
import edu.lysak.events.domain.EventDto;

import java.util.List;

public interface EventService {

    Long createEvent(EventDto eventDto);

    boolean updateEvent(Long id, EventDto eventDto);

    Event getEvent(Long id);

    boolean deleteEvent(Long id);

    List<Event> getAllEvents();

    List<Event> getAllEventsByTitle(String title);
}
