package com.example.event.activity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/activity")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ActivityResource {
    @Autowired
    private ActivityService activityService;

    
    @GetMapping("/{eventId}")
    public ResponseEntity<List<Activity>> getByEventId(@PathVariable Integer eventId) {
        // Gọi phương thức findByEventId trong ActivityRepository để lấy danh sách hoạt động theo eventId
        return ResponseEntity.status(HttpStatus.OK).body(activityService.findByEventId(eventId));
    }
    @GetMapping("/event/{eventId}/date/{date}")
    public ResponseEntity<List<Activity>> getByEventIdAndDate(@PathVariable Integer eventId, @PathVariable String date) {
        // Chuyển đổi chuỗi ngày thành đối tượng LocalDate
        LocalDate localDate = LocalDate.parse(date);

        // Gọi phương thức findByEventIdAndDate trong ActivityService để lấy danh sách hoạt động theo eventId và date
        return ResponseEntity.status(HttpStatus.OK).body(activityService.findByEventIdAndDate(eventId, localDate));
    }
}
