package com.example.event.activity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.event.event.Event;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    private String activityName;
    private String description;
    private String img;
    private Integer priority;
    private LocalDate date;
    // Getters and setters
}