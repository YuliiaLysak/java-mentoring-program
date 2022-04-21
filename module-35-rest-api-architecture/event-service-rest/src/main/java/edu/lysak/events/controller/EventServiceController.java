package edu.lysak.events.controller;

import edu.lysak.events.EventService;
import edu.lysak.events.domain.EventRequest;
import edu.lysak.events.domain.EventResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Tag(name = "Events API", description = "Events API provide the ability to manipulate the events")
public class EventServiceController {
    private final EventService eventService;

    public EventServiceController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(
            value = "/event/new",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Add new event", tags = {"event"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event created",
                    content = @Content(schema = @Schema(implementation = EventResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Event already exists")})
    public ResponseEntity<Long> addEvent(
            @Parameter(description = "Event to add. Cannot be null or empty.",
                    required = true, schema = @Schema(implementation = EventRequest.class))
            @Valid @RequestBody EventRequest eventRequest
    ) {
        Long id = eventService.createEvent(eventRequest);
        if (id == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok(id);
    }

    @PutMapping(
            value = "/event/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update the existing event", tags = {"event"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated",
                    content = @Content(schema = @Schema(implementation = EventResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Event not found")})
    public ResponseEntity<Void> updateEvent(
            @Parameter(description = "Id of the event to be update. Cannot be empty.",
                    required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Event to update. Cannot be null or empty.",
                    required = true, schema = @Schema(implementation = EventRequest.class))
            @Valid @RequestBody EventRequest eventRequest
    ) {
        if (!eventService.updateEvent(id, eventRequest)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/event/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Find event by id", tags = {"event"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event is found",
                    content = @Content(schema = @Schema(implementation = EventResponse.class))),
            @ApiResponse(responseCode = "404", description = "Event not found")})
    public ResponseEntity<EventResponse> findEventById(
            @Parameter(description = "Id of the event to be obtained. Cannot be empty.",
                    required = true)
            @PathVariable("id") Long id
    ) {
        EventResponse event = eventService.getEvent(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    @DeleteMapping(path = "/event/{id}")
    @Operation(summary = "Deletes an event", tags = {"event"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event deleted"),
            @ApiResponse(responseCode = "404", description = "Event not found")})
    public ResponseEntity<Void> deleteEventById(
            @Parameter(description = "Id of the event to be delete. Cannot be empty.",
                    required = true)
            @PathVariable("id") Long id
    ) {
        if (!eventService.deleteEvent(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get list of all events", tags = {"event"})
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping(value = "/event/title", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get list of all events by title", tags = {"event"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<List<EventResponse>> getAllEvents(@Parameter(description = "Event title") @RequestParam("title") String title) {
        List<EventResponse> events = eventService.getAllEventsByTitle(title);
        if (events.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(events);
    }
}
