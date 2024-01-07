package org.events.service;


import jakarta.persistence.EntityNotFoundException;
import org.events.entities.Event;
import org.events.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    EventSubscriptionService eventSubscriptionService;

    @Autowired
    EventRepository eventRepository;

    public ResponseEntity<List<Event>> getAllEvents() {
        return getAllEvents("id");
    }

    public ResponseEntity<List<Event>> getAllEvents(String field) {
        return ResponseEntity.ok(eventRepository.findAll(Sort.by(Sort.Direction.ASC, field))
                .stream()
                .filter(this::isEventBelongToUser)
                .toList());
    }

    public ResponseEntity<Event> getSpecificEventById(Integer id) {
        return eventRepository.findById(id)
                .filter(this::isEventBelongToUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity
                        .notFound()
                        .build());
    }

    public ResponseEntity<List<Event>> getEventsByNoParticipants() {
        List<Event> listOfEvents =
                eventRepository.findAll(Sort.by(Sort.Direction.ASC, "participants.size()"));
        return ResponseEntity.ok(listOfEvents);
    }

    public ResponseEntity<Integer> addEvent(Event event) {
        return Optional.ofNullable(event)
                .filter((Event e) -> e.getStartTime().isBefore(e.getEndTime()))
                .map(this::createNewEvent)
                .map((Event e) -> {
                    Event savedEvent = eventRepository.save(event);
                    eventSubscriptionService.addRemainderEvent(savedEvent);
                    return savedEvent;
                })
                .map((Event e) -> ResponseEntity.ok(e.getId()))
                .orElse(ResponseEntity.internalServerError().build());
    }

    public ResponseEntity<Event> updateEvent(Event event) {
        return Optional.of(event)
                .filter(this::isEventExistAndOwnedByUser)
                .map((Event e) -> {
                    Event updatedEvent = eventRepository.save(e);
                    eventSubscriptionService.updateRemainderEvent(updatedEvent);
                    return e;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity
                        .notFound()
                        .build());
    }

    public ResponseEntity<Event> subscribeEvent(Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return eventRepository.findById(id)
                .filter((Event e) -> isEventBelongToUser(e) || e.getParticipants().contains(username))
                .map((Event e) -> {
                    eventSubscriptionService.subscribeToEvent(id, username);
                    return ResponseEntity.ok(e);
                })
                .orElse(ResponseEntity.internalServerError().build());
    }

    public ResponseEntity<Event> deleteEvent(Integer id) {
        try {
            Event e = eventRepository.findById(id)
                    .filter(this::isEventBelongToUser)
                    .orElseThrow(EntityNotFoundException::new);
            eventRepository.deleteById(id);
            eventSubscriptionService.cancelRemainderEvent(e);

            return ResponseEntity.ok(e);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private Event createNewEvent(Event e) {
        return Event.builder()
                .creator(SecurityContextHolder.getContext().getAuthentication().getName())
                .location(e.getLocation())
                .title(e.getTitle())
                .creationTime(Instant.now())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .build();
    }

    private boolean isEventExistAndOwnedByUser(Event event) {
        return eventRepository.findById(event.getId())
                .filter(this::isEventBelongToUser)
                .isPresent();
    }

    private boolean isEventBelongToUser(Event e) {
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch((String s) -> s.equals("ROLE_ADMIN"));

        return isAdmin || e.getCreator().equals(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
