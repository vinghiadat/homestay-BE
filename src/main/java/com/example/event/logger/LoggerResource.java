package com.example.event.logger;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/logger")
@CrossOrigin(origins = {"http://localhost:4200"})
public class LoggerResource {
    @Autowired
    private LoggerService loggerService;

    @GetMapping()
    public ResponseEntity<List<Logger>> getAllLogger() {
        return ResponseEntity.status(HttpStatus.OK).body(loggerService.getAllLogger());
    }
}
