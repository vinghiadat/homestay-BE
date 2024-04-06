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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Không được bỏ trống sự kiện")
    private Event event;
    @NotBlank(message = "Không được bỏ trống tên hoạt động")
    private String activityName;
    private String description;
    private String img;
    @NotNull(message = "Không được bỏ trống giờ hoạt động")
    private LocalDateTime dateTime;
    // Getters and setters
}