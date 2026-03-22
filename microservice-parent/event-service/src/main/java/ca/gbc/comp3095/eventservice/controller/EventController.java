package ca.gbc.comp3095.eventservice.controller;

import ca.gbc.comp3095.eventservice.service.WellnessGatewayService;
import ca.gbc.comp3095.eventservice.dto.EventRequest;
import ca.gbc.comp3095.eventservice.dto.EventResponse;
import ca.gbc.comp3095.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final WellnessGatewayService wellnessGatewayService;

    // create - POST /api/event
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest eventRequest){
        EventResponse response = eventService.createEvent(eventRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // get all (optional filters) - GET /api/event
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponse> getAllEvents(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) LocalDateTime dateFrom,
            @RequestParam(required = false) LocalDateTime dateTo){
        return eventService.findEvents(location, dateFrom, dateTo);
    }

    // get by id - GET /api/event/{eventId}
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponse getEventById(@PathVariable("eventId") Long eventId){
        return eventService.getEventById(eventId);
    }

    // update - PUT /api/event/{eventId}
    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") Long eventId,
                                         @RequestBody EventRequest eventRequest){
        EventResponse updatedEvent = eventService.updateEvent(eventId, eventRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/event/" + updatedEvent.getId());
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    // delete - DELETE /api/event/{eventId}
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") Long eventId){
        eventService.deleteEvent(eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // create - POST /api/event/{id}/register?studentId=...
    @PostMapping("/{eventId}/register")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void register(@PathVariable Long eventId, @RequestParam String studentId) {
        eventService.register(eventId, studentId);
    }

    // delete - DELETE /api/event/{id}/register?studentId=...
    @DeleteMapping("/{eventId}/register")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregister(@PathVariable Long eventId, @RequestParam String studentId) {
        eventService.unregister(eventId, studentId);
    }

}
