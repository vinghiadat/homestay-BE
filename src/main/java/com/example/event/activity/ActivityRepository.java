package com.example.event.activity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    List<Activity> findByEventIdOrderByDateTimeAsc(Integer eventId);
    List<Activity> findByEventIdAndDateTimeOrderByDateTimeAsc(Integer eventId, LocalDateTime date);
    List<Activity> findByEventId(Integer eventId);
    List<Activity> findByDateTimeAndEventId(LocalDateTime dateTime, Integer eventId);
}
