package com.example.event.activity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.event.event.Event;
import com.example.event.event.EventRepository;
import com.example.event.exception.InvalidValueException;
import com.example.event.exception.NotFoundException;
import com.example.event.logger.LoggerService;
import com.example.event.role.Role;
import com.example.event.user.User;
import com.example.event.user.UserRepository;

import jakarta.validation.Valid;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private LoggerService loggerService;
    
    public List<Activity> findByEventId( Integer eventId, String activityName) {
        // Gọi phương thức findByEventId trong ActivityRepository để lấy danh sách hoạt động theo eventId
        List<Activity> activities = activityRepository.findByEventIdOrderByDateTimeAsc(eventId);
        if(activityName !=null)
            activities.removeIf((a) -> !a.getActivityName().contains(activityName));
        return activities;
    }
     public List<Activity> findByEventIdAndDate(Integer eventId, LocalDate date) {
        return activityRepository.findByEventIdAndDate(eventId, date);
    }
    public void addActivity(Activity activity,
    Integer userId) {
        Event e = eventRepository.findById(activity.getEvent().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại sự kiện phù hợp với hoạt động này"));
        // Kiểm tra xem thời gian hoạt động có nằm trong khoảng thời gian của sự kiện không
        LocalDateTime activityDateTime = activity.getDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        if (activityDateTime.isBefore(e.getStartDateTime()) || activityDateTime.isAfter(e.getEndDateTime())) {
            throw new InvalidValueException("Thời gian của hoạt đồng phải từ "+e.getStartDateTime().format(formatter)+" đến "+e.getEndDateTime().format(formatter));
        }
        // Kiểm tra xem có hoạt động nào khác cùng thời gian không
        List<Activity> conflictingActivities = activityRepository.findByDateTimeAndEventId(activityDateTime,e.getId());
        if (!conflictingActivities.isEmpty()) {
            throw new InvalidValueException("Đã tồn tại hoạt động khác vào thời gian này trong cùng 1 sự kiện");
        }
        String content = "- Thêm hoạt động \""+activity.getActivityName()+"\" cho sự kiện \""+e.getEventName()+"\"";
        addLogger(userId, content);
        // Lưu hoạt động vào cơ sở dữ liệu
        activityRepository.save(activity);
        
    }
    public void updateActivityById(Integer id,Integer userId,Activity activity) {
        Activity activity2 = activityRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại hoạt động với id: "+id));
        Event e = eventRepository.findById(activity.getEvent().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại sự kiện phù hợp với hoạt động này"));
        // Kiểm tra xem thời gian hoạt động có nằm trong khoảng thời gian của sự kiện không
        LocalDateTime activityDateTime = activity.getDateTime();
        if (activityDateTime.isBefore(e.getStartDateTime()) || activityDateTime.isAfter(e.getEndDateTime())) {
            throw new InvalidValueException("Thời gian hoạt động không nằm trong khoảng thời gian của sự kiện");
        }
        // Kiểm tra xem có hoạt động nào khác cùng thời gian không
        List<Activity> conflictingActivities = activityRepository.findByDateTimeAndEventId(activityDateTime,e.getId());
        conflictingActivities.removeIf((a) -> a.getId() == id);
        if (!conflictingActivities.isEmpty()) {
            throw new InvalidValueException("Đã tồn tại hoạt động khác vào thời gian này trong cùng 1 sự kiện");
        }
        activity2.setActivityName(activity.getActivityName());
        activity2.setDateTime(activity.getDateTime());
        activity2.setDescription(activity.getDescription());
        activity2.setEvent(e);
        activity2.setImg(activity.getImg());
        String content = "- Cập nhật hoạt động \""+activity.getActivityName()+"\" cho sự kiện \""+e.getEventName()+"\"";
        addLogger(userId, content);
        // Lưu hoạt động vào cơ sở dữ liệu
        activityRepository.save(activity2);
    }
    public void deleteActivityById(Integer id,Integer userId) {
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại hoạt động với id: "+id));
        Event event = eventRepository.findById(activity.getEvent().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại sự kiện với hoạt động với id: "+id));
        String content = "- Xóa hoạt động \""+activity.getActivityName()+"\" của sự kiện \""+event.getEventName()+"\"";
        addLogger(userId,content);
        activityRepository.delete(activity);
    }
    private void addLogger(Integer userId,String content) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Không tồn tại user "));
        String roleName = new String();
            boolean lastElement = false;

            // Lặp qua các role
            for (int i = 0; i < user.getRoles().size(); i++) {
                Role r = user.getRoles().get(i);
                roleName += r.getName();
                
                // Kiểm tra nếu phần tử hiện tại là phần tử cuối cùng
                if (i == user.getRoles().size() - 1) {
                    lastElement = true;
                }
                
                // Nếu không phải phần tử cuối cùng, thêm dấu phẩy
                if (!lastElement) {
                    roleName += " ,";
                }
            }
        loggerService.addLogger(user,content,roleName);
    }
}
