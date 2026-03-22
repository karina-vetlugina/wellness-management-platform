package ca.gbc.comp3095.eventservice.service;

import ca.gbc.comp3095.eventservice.dto.EventRequest;
import ca.gbc.comp3095.eventservice.dto.EventResponse;
import ca.gbc.comp3095.eventservice.model.Event;
import ca.gbc.comp3095.eventservice.repository.EventRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    // mapping helpers (entity <-> DTO)
    private EventResponse toResponse(Event e){
        return EventResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .location(e.getLocation())
                .capacity(e.getCapacity())
                .registeredStudents(e.getRegisteredStudents())
                .build();
    }

    private Event toEntity(EventRequest req){
        return Event.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .eventDate(req.getEventDate())
                .location(req.getLocation())
                .capacity(req.getCapacity())
                .build();
    }

    // CRUD
    @Override
    public EventResponse createEvent(EventRequest request){
        log.debug("Creating event {}", request);
        Event saved =  eventRepository.save(toEntity(request));
        log.debug("Saved event {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long eventId){
        log.debug("Fetching event id={}", eventId);
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
        return toResponse(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents(){
        log.debug("Fetching all events");
        return eventRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> findEvents(String location, LocalDateTime dateFrom, LocalDateTime dateTo) {
        log.debug("Searching events location='{}', from={}, to={}", location, dateFrom, dateTo);

        if (location != null && !location.isBlank() && dateFrom != null && dateTo != null){
            return eventRepository.findAllByEventDateBetween(dateFrom, dateTo).stream()
                    .filter(e -> e.getLocation() != null &&
                            e.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .map(this::toResponse)
                    .toList();
        }else if (location != null && !location.isBlank()){
            return eventRepository.findAllByLocationIgnoreCaseContaining(location).stream()
                    .map(this::toResponse).toList();
        }else if (dateFrom != null && dateTo != null){
            return eventRepository.findAllByEventDateBetween(dateFrom, dateTo).stream()
                    .map(this::toResponse).toList();
        }else{
            return getAllEvents();
        }
    }

    @Override
    public EventResponse updateEvent(Long eventId, EventRequest request){
        log.debug("Updating event id={}", eventId);

        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        // capacity rule: can't shrink below current registrations
        if (request.getCapacity() < e.getRegisteredStudents().size()){
            throw new IllegalArgumentException("Capacity can not be less than current registrations");
        }

        e.setTitle(request.getTitle());
        e.setDescription(request.getDescription());
        e.setEventDate(request.getEventDate());
        e.setLocation(request.getLocation());
        e.setCapacity(request.getCapacity());

        Event saved = eventRepository.save(e);
        return toResponse(saved);
    }

    @Override
    public void deleteEvent(Long eventId){
        log.debug("Deleting event id={}", eventId);
        if (!eventRepository.existsById(eventId)){
            throw new IllegalArgumentException("Event not found: " + eventId);
        }
        eventRepository.deleteById(eventId);
    }

    @Override
    public void register(Long eventId, String studentId) {
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
        if (e.getRegisteredStudents().size() >= e.getCapacity()) {
            throw new IllegalArgumentException("Event is at capacity");
        }
        if (!e.getRegisteredStudents().add(studentId)) {
            // already registered
            return;
        }
        eventRepository.save(e);
    }

    @Override
    public void unregister(Long eventId, String studentId) {
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
        if (e.getRegisteredStudents().remove(studentId)) {
            eventRepository.save(e);
        }
    }

}
