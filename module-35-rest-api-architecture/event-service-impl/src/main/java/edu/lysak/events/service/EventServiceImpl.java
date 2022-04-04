package edu.lysak.events.service;

import edu.lysak.events.EventService;
import edu.lysak.events.domain.Event;
import edu.lysak.events.domain.EventDto;
import edu.lysak.events.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Long createEvent(EventDto eventDto) {
        Optional<Event> eventFromDb = eventRepository.findByTitleAndSpeakerAndDateTime(
                eventDto.getTitle(),
                eventDto.getSpeaker(),
                eventDto.getDateTime()
        );
        if (eventFromDb.isPresent()) {
            return null;
        }
        return eventRepository.save(mapEvent(eventDto)).getId();
    }

    @Override
    public boolean updateEvent(Long id, EventDto eventDto) {
        if (eventRepository.existsById(id)) {
            eventRepository.save(mapEvent(eventDto));
            return true;
        }

        return false;
    }

    private Event mapEvent(EventDto eventDto) {
        Event event = new Event();
        event.setTitle(eventDto.getEventType());
        event.setPlace(eventDto.getPlace());
        event.setSpeaker(eventDto.getSpeaker());
        event.setEventType(eventDto.getEventType());
        event.setDateTime(eventDto.getDateTime());
        return event;
    }

    @Override
    public Event getEvent(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    @Override
    public boolean deleteEvent(Long id) {
        int affectedRows = eventRepository.delete(id);
        return affectedRows != 0;
    }

    @Override
    public List<Event> getAllEvents() {
        return (List<Event>) eventRepository.findAll();
    }

    @Override
    public List<Event> getAllEventsByTitle(String title) {
        return (List<Event>) eventRepository.findAllByTitle(title);
    }
}
