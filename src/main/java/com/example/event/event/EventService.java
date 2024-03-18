package com.example.event.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    public List<Event> getEventsByOrganizerId(Integer organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }
    public Event getEventById(Integer eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }
    public List<Event> getEventsByStatus(EventStatus status) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        switch (status) {
            case SAU:
                return eventRepository.findByStartDateTimeAfter(now);
            case DANG:
                return eventRepository.findByStartDateTimeBeforeAndEndDateTimeAfter(now, now);
            case TRUOC:
                return eventRepository.findByEndDateTimeBefore(now);
            default:
                return new ArrayList<>();
        }
    }
    public List<Event> getEventsByStatusAndOrganizerIdAndName(
         EventStatus eventStatus,
         Integer organizerId,
         String eventName
    ) {
        LocalDateTime now = LocalDateTime.now();
        List<Event> events = eventRepository.findAll();
        if(eventStatus !=null) {
            switch (eventStatus) {
                case SAU:
                    events.retainAll(eventRepository.findByStartDateTimeAfter(now));
                    break;
                    
                case DANG:
                    events.retainAll(eventRepository.findByStartDateTimeBeforeAndEndDateTimeAfter(now, now));
                    break;
                case TRUOC:
                    events.retainAll(eventRepository.findByEndDateTimeBefore(now));
                    break;
                default:
                    break;
                    
            }
        }
        if (organizerId != null) {
            events.removeIf(e ->  !e.getOrganizer().getId().equals(organizerId));
        }
        if (eventName != null && !eventName.isEmpty()) {
            events.removeIf(e -> !e.getEventName().toLowerCase().contains(eventName.toLowerCase()));
        }
        return events;
    }
    public List<Event> getEventsByOrganizerIdExcludingEventId(Integer organizerId, Integer eventId) {
        return eventRepository.findTop5ByOrganizerIdAndIdNot(organizerId, eventId);
    }
}
