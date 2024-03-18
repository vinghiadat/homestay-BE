package com.example.event.activity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    
    public List<Activity> findByEventId(@PathVariable Integer eventId) {
        // Gọi phương thức findByEventId trong ActivityRepository để lấy danh sách hoạt động theo eventId
        return activityRepository.findByEventIdOrderByPriorityAsc(eventId);
    }
     public List<Activity> findByEventIdAndDate(Integer eventId, LocalDate date) {
        return activityRepository.findByEventIdAndDateOrderByPriorityAsc(eventId, date);
    }
}
