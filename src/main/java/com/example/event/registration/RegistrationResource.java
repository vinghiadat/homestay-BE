package com.example.event.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
