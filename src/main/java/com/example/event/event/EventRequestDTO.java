package com.example.event.event;

import java.time.LocalDateTime;

import com.example.event.organizer.Organizer;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {
    private String eventName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;
    private Integer maxQuantity;
    private String img;
    private Integer organizerId;
    private Boolean status;
}
