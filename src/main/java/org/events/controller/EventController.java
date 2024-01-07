package org.events.controller;

import org.events.entities.Event;
import org.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("event")
public class EventController {

    @Autowired
    EventService eventService;

    @GetMapping("getAllEvents")
    public ResponseEntity<List<Event>>  getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("getAllEvents/{field}")
    public ResponseEntity<List<Event>>  getAllEvents(@PathVariable String field) {
        return eventService.getAllEvents(field);
    }

    @GetMapping("getAllEventsByNoParticipants")
    public ResponseEntity<List<Event>> getAllEventByNumberOfParticipants() {
        return eventService.getEventsByNoParticipants();
    }

    @GetMapping("getSpecificEvent/{id}")
    public ResponseEntity<Event> getSpecificEvent(@PathVariable Integer id) {
        return eventService.getSpecificEventById(id);
    }

    @PostMapping("addEvent")
    public ResponseEntity<Integer> addEvent(@RequestBody Event event) {
        return eventService.addEvent(event);
    }

    @PutMapping("updateEvent")
    public ResponseEntity<Event> updateEvent(@RequestBody Event event) {
        return eventService.updateEvent(event);
    }

    @PutMapping("subscribeEvent/{id}")
    public ResponseEntity<Event> subscribeEvent(@PathVariable Integer id) {
        return eventService.subscribeEvent(id);
    }

    @DeleteMapping("deleteEvent/{id}") // delete specific event
    public ResponseEntity<Event> deleteEvent(@PathVariable Integer id) {
        return eventService.deleteEvent(id);
    }
}
