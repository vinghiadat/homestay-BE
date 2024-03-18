package com.example.event.activity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    List<Activity> findByEventIdOrderByPriorityAsc(Integer eventId);
    List<Activity> findByEventIdAndDateOrderByPriorityAsc(Integer eventId, LocalDate date);

}
