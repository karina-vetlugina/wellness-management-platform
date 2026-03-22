package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.dto.EventRequest;
import ca.gbc.comp3095.eventservice.dto.EventResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventResponse createEvent(EventRequest request);
    EventResponse getEventById(Long eventId);
    List<EventResponse> getAllEvents();
    List<EventResponse> findEvents(String location, LocalDateTime dateFrom, LocalDateTime dateTo);
    EventResponse updateEvent(Long eventId, EventRequest request);
    void deleteEvent(Long eventId);
    void register(Long eventId, String studentId);
    void unregister(Long eventId, String studentId);

}
