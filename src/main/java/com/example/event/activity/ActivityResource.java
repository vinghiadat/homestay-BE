package com.example.event.activity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/activity")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ActivityResource {
    @Autowired
    private ActivityService activityService;

    
    
    @GetMapping("/{eventId}")
    public ResponseEntity<List<Activity>> getByEventId(@PathVariable Integer eventId, @RequestParam(required = false) String activityName) {
        // Gọi phương thức findByEventId trong ActivityRepository để lấy danh sách hoạt động theo eventId
        return ResponseEntity.status(HttpStatus.OK).body(activityService.findByEventId(eventId,activityName));
    }
    @GetMapping("/event/{eventId}/date/{date}")
    public ResponseEntity<List<Activity>> getByEventIdAndDate(@PathVariable Integer eventId, @PathVariable String date) {
        // Chuyển đổi chuỗi ngày thành đối tượng LocalDate
        LocalDate localDate = LocalDate.parse(date);

        // Gọi phương thức findByEventIdAndDate trong ActivityService để lấy danh sách hoạt động theo eventId và date
        return ResponseEntity.status(HttpStatus.OK).body(activityService.findByEventIdAndDate(eventId, localDate));
    }
    //Cập nhật có 2 loại PUT & PATCH
    @PostMapping("/user/{userId}")
    public ResponseEntity<Void> addActivity(@PathVariable Integer userId,@Valid @RequestBody Activity activity) {
        activityService.addActivity(activity,userId);
        return ResponseEntity.noContent().build();
    }
    //Cập nhật có 2 loại PUT & PATCH
    @PatchMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> updateActivityById(@PathVariable Integer id,@PathVariable Integer userId,@Valid @RequestBody Activity activity) {
        activityService.updateActivityById(id,userId,activity);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> deleteActivityById(@PathVariable Integer id,@PathVariable Integer userId) {
        activityService.deleteActivityById(id,userId);
        return ResponseEntity.noContent().build();
    }
}
