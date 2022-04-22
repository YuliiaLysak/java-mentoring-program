package edu.lysak.events;

import edu.lysak.events.domain.EventRequest;
import edu.lysak.events.domain.EventResponse;

import java.util.List;

public interface EventService {

    Long createEvent(EventRequest eventRequest);

    boolean updateEvent(Long id, EventRequest eventRequest);

    EventResponse getEvent(Long id);

    boolean deleteEvent(Long id);

    List<EventResponse> getAllEvents();

    List<EventResponse> getAllEventsByTitle(String title);
}
