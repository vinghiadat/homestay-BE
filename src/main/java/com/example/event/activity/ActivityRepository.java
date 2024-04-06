package com.example.event.activity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    List<Activity> findByEventIdOrderByDateTimeAsc(Integer eventId);
    List<Activity> findByEventIdAndDateTimeOrderByDateTimeAsc(Integer eventId, LocalDateTime date);
    List<Activity> findByEventId(Integer eventId);
    List<Activity> findByDateTimeAndEventId(LocalDateTime dateTime, Integer eventId);
    @Query("SELECT a FROM Activity a WHERE a.event.id = :eventId AND DATE(a.dateTime) = :searchDate")
    List<Activity> findByEventIdAndDate(@Param("eventId") Integer eventId, @Param("searchDate") LocalDate searchDate);
    List<Activity> findByEventIdAndActivityNameOrderByDateTimeAsc(Integer eventId, String activityName);
}
