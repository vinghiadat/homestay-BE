package com.example.event.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.event.organizer.Organizer;

@RestController
@RequestMapping("api/v1/event")
@CrossOrigin(origins = {"http://localhost:4200"})
public class EventResource {
    @Autowired
    private EventService eventService;

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable Integer eventId) {
        Event event = eventService.getEventById(eventId);

        if (event != null) {
            return ResponseEntity.ok(event);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping()
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/by-organizer")
    public List<Event> getEventsByOrganizerId(@RequestParam Integer organizerId) {
        return eventService.getEventsByOrganizerId(organizerId);
    }
    @GetMapping("/status")
    public ResponseEntity<List<Event>> getEventsByStatus(@RequestParam EventStatus status) {
        List<Event> events = eventService.getEventsByStatus(status);
        return ResponseEntity.ok(events);
    }
    @GetMapping("/filter")
    public ResponseEntity<List<Event>> getEventsByStatusAndOrganizerIdAndName(
        @RequestParam(required = false) EventStatus eventStatus,
        @RequestParam(required = false) Integer organizerId,
        @RequestParam(required = false) String eventName
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventsByStatusAndOrganizerIdAndName(eventStatus,organizerId,eventName));
    }
    @GetMapping("/by-organizer-excluding")
    public ResponseEntity<List<Event>> getEventsByOrganizerIdExcludingEventId(@RequestParam Integer organizerId, @RequestParam Integer eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventsByOrganizerIdExcludingEventId(organizerId, eventId));
    }

    

}
