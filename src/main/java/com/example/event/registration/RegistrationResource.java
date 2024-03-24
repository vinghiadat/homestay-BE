package com.example.event.registration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.event.event.Event;
import com.example.event.event.EventStatus;

@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:4401"})
@RequestMapping("api/v1/registration")
public class RegistrationResource {
    @Autowired
    private RegistrationService registrationService;   

    @PostMapping
    public ResponseEntity<Void> registerEvent(@RequestBody Registration registration) {
        registrationService.registerUserForEvent(registration);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/check-exist")
    public ResponseEntity<Boolean> isCheckEventIdAndUserId(@RequestParam(required = true) Integer userId,@RequestParam(required = true) Integer eventId) {
        
        return ResponseEntity.status(HttpStatus.OK).body(registrationService.isCheckEventIdAndUserId(userId, eventId));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Event>> getEventByUserId(@PathVariable Integer userId,
    @RequestParam(required = false) EventStatus eventStatus,
        @RequestParam(required = false) Integer organizerId,
        @RequestParam(required = false) String eventName) {
        return ResponseEntity.status(HttpStatus.OK).body(registrationService.getEventByUserId(userId,eventStatus,organizerId,eventName));
    }
    @GetMapping("/user/{userId}/event/{eventId}")
    public ResponseEntity<Registration> getRegistrationByUserIdAndEventId(@PathVariable("userId") Integer userId,@PathVariable("eventId") Integer eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(registrationService.getRegistrationByUserIdAndEventId(userId,eventId));
    }
}
