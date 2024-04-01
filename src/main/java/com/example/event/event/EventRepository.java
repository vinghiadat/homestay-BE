package com.example.event.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event,Integer> {
    List<Event> findByOrganizerId(Integer organizerId);
    List<Event> findByStartDateTimeAfter(LocalDateTime startDateTime);
    List<Event> findByStartDateTimeBeforeAndEndDateTimeAfter(LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<Event> findByEndDateTimeBefore(LocalDateTime endDateTime);
    List<Event> findTop5ByOrganizerIdAndIdNot(Integer organizerId, Integer eventId);
    Boolean existsByOrganizerId(Integer organizerId);
    List<Event> findAll(Specification<Event> spec);
}
