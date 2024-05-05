package com.example.homestay.booking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/booking")
@CrossOrigin(origins = {"http://localhost:4200"})
public class BookingResource {
    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  LocalDate registrationDate,
        @RequestParam(required = false) String username
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAllBookings(registrationDate,username));
    }
    @PostMapping
    public ResponseEntity<Void> addBooking(@RequestBody @Valid Booking booking) {
        bookingService.addBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //Cập nhật có 2 loại PUT & PATCH
    @PatchMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> updateBookingById(@PathVariable Integer id,@PathVariable Integer userId,@RequestBody Booking booking) {
        bookingService.updateBookingById(id,userId,booking);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<List<Booking>> getBookingsByUserId(
        @PathVariable Integer id,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  LocalDate registrationDate,
        @RequestParam(required = false) Integer typeId,
        @RequestParam(required = false) String roomName
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getBookingsByUserId(id, registrationDate, typeId,roomName));
    }
}
