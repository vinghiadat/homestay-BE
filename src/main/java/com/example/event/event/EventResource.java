package com.example.event.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.event.organizer.Organizer;

import jakarta.validation.Valid;

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
    @PostMapping("/user/{userId}")
    public ResponseEntity<Void> addEvent(@Valid @RequestBody EventRequestDTO eventRequestDTO,
    @PathVariable Integer userId) {
        
        eventService.addEvent(eventRequestDTO,userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //Cập nhật có 2 loại PUT & PATCH
    @PatchMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> updateEventById(@PathVariable Integer id,@PathVariable Integer userId,@Valid @RequestBody EventRequestDTO eventRequestDTO) {
        eventService.updateEventById(id,userId,eventRequestDTO);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> deleteEventById(@PathVariable Integer id,@PathVariable Integer userId) {
        eventService.deleteEventById(id,userId);
        return ResponseEntity.noContent().build();
    }
    

}
