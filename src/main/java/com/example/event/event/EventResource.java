package com.example.event.event;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/event")
public class EventResource {
    @Autowired
    private EventService eventService;

    @GetMapping()
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

}
