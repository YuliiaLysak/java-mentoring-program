package edu.lysak.events.service;

import edu.lysak.events.EventService;
import edu.lysak.events.domain.Event;
import edu.lysak.events.domain.EventRequest;
import edu.lysak.events.domain.EventResponse;
import edu.lysak.events.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Long createEvent(EventRequest eventRequest) {
        Optional<Event> eventFromDb = eventRepository.findByTitleAndSpeakerAndDateTime(
                eventRequest.getTitle(),
                eventRequest.getSpeaker(),
                eventRequest.getDateTime()
        );
        if (eventFromDb.isPresent()) {
            return null;
        }
        return eventRepository.save(mapEvent(eventRequest)).getId();
    }

    @Override
    public boolean updateEvent(Long id, EventRequest eventRequest) {
        if (eventRepository.existsById(id)) {
            eventRepository.save(mapEvent(eventRequest));
            return true;
        }

        return false;
    }

    @Override
    public EventResponse getEvent(Long id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            return null;
        }
        return mapEventResponse(event.get());
    }

    @Override
    public boolean deleteEvent(Long id) {
        int affectedRows = eventRepository.delete(id);
        return affectedRows != 0;
    }

    @Override
    public List<EventResponse> getAllEvents() {
        List<Event> allEvents = eventRepository.findAllEvents();
        return allEvents.stream()
                .map(this::mapEventResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponse> getAllEventsByTitle(String title) {
        List<Event> allByTitle = eventRepository.findAllByTitle(title);
        return allByTitle.stream()
                .map(this::mapEventResponse)
                .collect(Collectors.toList());
    }

    private Event mapEvent(EventRequest eventRequest) {
        Event event = new Event();
        event.setTitle(eventRequest.getEventType());
        event.setPlace(eventRequest.getPlace());
        event.setSpeaker(eventRequest.getSpeaker());
        event.setEventType(eventRequest.getEventType());
        event.setDateTime(eventRequest.getDateTime());
        return event;
    }

    private EventResponse mapEventResponse(Event event) {
        EventResponse eventResponse = new EventResponse();
        eventResponse.setTitle(event.getEventType());
        eventResponse.setPlace(event.getPlace());
        eventResponse.setSpeaker(event.getSpeaker());
        eventResponse.setEventType(event.getEventType());
        eventResponse.setDateTime(event.getDateTime());
        return eventResponse;
    }
}
