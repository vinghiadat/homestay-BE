package com.example.homestay.booking;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/booking")
@CrossOrigin(origins = {"http://localhost:4200"})
public class BookingResource {
    
}
