package com.example.event.event;

import java.time.LocalDateTime;
import java.util.List;

import com.example.event.activity.Activity;
import com.example.event.organizer.Organizer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String eventName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;
    private Integer maxQuantity;
    private Boolean status = true;
    private int totalRegistered = 0; // Tổng số người đăng ký
    private int totalAttended = 0; // Tổng số người tham gia thực tế
    private String img;
    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;
}
