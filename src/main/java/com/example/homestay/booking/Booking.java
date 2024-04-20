package com.example.homestay.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.homestay.homestayroom.Room;
import com.example.homestay.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    private LocalDate registrationDate = LocalDate.now();
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer status = 0;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User users;
}
