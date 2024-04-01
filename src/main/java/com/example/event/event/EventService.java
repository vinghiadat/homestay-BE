package com.example.event.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.event.activity.Activity;
import com.example.event.activity.ActivityRepository;
import com.example.event.exception.AlreadyExistsException;
import com.example.event.exception.InvalidValueException;
import com.example.event.exception.NotFoundException;
import com.example.event.logger.LoggerService;
import com.example.event.organizer.Organizer;
import com.example.event.organizer.OrganizerRepository;
import com.example.event.registration.RegistrationRepository;
import com.example.event.role.Role;
import com.example.event.user.User;
import com.example.event.user.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private OrganizerRepository organizerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoggerService loggerService;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private ActivityRepository activityRepository;
    public List<Event> getAllEvents() {
        Specification<Event> spec = (root, query, cb) -> {
            query.orderBy(
                cb.asc(root.get("organizer").get("id")),
                cb.asc(root.get("startDateTime")),
                cb.asc(root.get("endDateTime"))
            );
            return cb.conjunction();
        };
        return eventRepository.findAll(spec);
    }
    public List<Event> getEventsByOrganizerId(Integer organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }
    public Event getEventById(Integer eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }
    public List<Event> getEventsByStatus(EventStatus status) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        switch (status) {
            case SAU:
                return eventRepository.findByStartDateTimeAfter(now);
            case DANG:
                return eventRepository.findByStartDateTimeBeforeAndEndDateTimeAfter(now, now);
            case TRUOC:
                return eventRepository.findByEndDateTimeBefore(now);
            default:
                return new ArrayList<>();
        }
    }
    public void deleteEventById(Integer id, Integer userId) {
        if(registrationRepository.existsByEventId(id)) {
            throw new AlreadyExistsException("Sự kiện này đang được sử dụng trong phần đăng ký sự kiện của khách hàng");
        }
        Event e = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại sự kiện với id "+id));
        if(userId!=null) {
            User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Không tồn tại user"));
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
            loggerService.addLogger(user, "- Xóa sự kiện: "+e.getEventName(),roleName);
            eventRepository.deleteById(id);
            List<Activity> activities = activityRepository.findByEventId(id);
            if(!activities.isEmpty()) {
                for (Activity a : activities) {
                    activityRepository.delete(a);
                }
            }
        } 
        
    }
    public List<Event> getEventsByStatusAndOrganizerIdAndName(
         EventStatus eventStatus,
         Integer organizerId,
         String eventName
    ) {
        LocalDateTime now = LocalDateTime.now();
        Specification<Event> spec = (root, query, cb) -> {
            query.orderBy(
                cb.asc(root.get("organizer").get("id")),
                cb.asc(root.get("startDateTime")),
                cb.asc(root.get("endDateTime"))
            );
            return cb.conjunction();
        };
        
        List<Event> events = eventRepository.findAll(spec);
        if(eventStatus !=null) {
            switch (eventStatus) {
                case SAU:
                    events.retainAll(eventRepository.findByStartDateTimeAfter(now));
                    break;
                    
                case DANG:
                    events.retainAll(eventRepository.findByStartDateTimeBeforeAndEndDateTimeAfter(now, now));
                    break;
                case TRUOC:
                    events.retainAll(eventRepository.findByEndDateTimeBefore(now));
                    break;
                default:
                    break;
                    
            }
        }
        if (organizerId != null) {
            events.removeIf(e ->  !e.getOrganizer().getId().equals(organizerId));
        }
        if (eventName != null && !eventName.isEmpty()) {
            events.removeIf(e -> !e.getEventName().toLowerCase().contains(eventName.toLowerCase()));
        }
        return events;
    }
    public List<Event> getEventsByOrganizerIdExcludingEventId(Integer organizerId, Integer eventId) {
        return eventRepository.findTop5ByOrganizerIdAndIdNot(organizerId, eventId);
    }
    @Transactional
    public void addEvent( EventRequestDTO eventRequestDTO,
    Integer userId) {
        Organizer o = organizerRepository.findById(eventRequestDTO.getOrganizerId()).orElseThrow(() -> new NotFoundException("Không tồn tại nhà tổ chức với id: "+eventRequestDTO.getOrganizerId()));
        Event event = new Event();
        event.setDescription(eventRequestDTO.getDescription());
        event.setEndDateTime(eventRequestDTO.getEndDateTime());
        event.setStartDateTime(eventRequestDTO.getStartDateTime());
        event.setEventName(eventRequestDTO.getEventName());
        event.setMaxQuantity(eventRequestDTO.getMaxQuantity());
        event.setImg(eventRequestDTO.getImg());
        event.setOrganizer(o);
        if(event.getStartDateTime().isAfter(event.getEndDateTime())) {
            throw new InvalidValueException("Ngày bắt đầu phải bé hơn ngày kết thúc");
        }
        
        if(event.getMaxQuantity()<=0) {
            throw new InvalidValueException("Số lượng tối đa phải >=0");
        }
        String content = "- Thêm sự kiện "+event.getEventName()+" thuộc nhà tổ chức "+o.getOrganizerName();
        eventRepository.save(event);
        addLogger(userId,content);
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
    public void updateEventById(Integer id, Integer userId,EventRequestDTO eventRequestDTO) {
        Organizer o = organizerRepository.findById(eventRequestDTO.getOrganizerId()).orElseThrow(() -> new NotFoundException("Không tồn tại nhà tổ chức với id: "+eventRequestDTO.getOrganizerId()));
        Event event = new Event();
        event.setDescription(eventRequestDTO.getDescription());
        event.setEndDateTime(eventRequestDTO.getEndDateTime());
        event.setStartDateTime(eventRequestDTO.getStartDateTime());
        event.setEventName(eventRequestDTO.getEventName());
        event.setMaxQuantity(eventRequestDTO.getMaxQuantity());
        event.setImg(eventRequestDTO.getImg());
        event.setOrganizer(o);
        event.setStatus(eventRequestDTO.getStatus());
        if(event.getStartDateTime().isAfter(event.getEndDateTime())) {
            throw new InvalidValueException("Ngày bắt đầu phải bé hơn ngày kết thúc");
        }
        
        if(event.getMaxQuantity()<=0) {
            throw new InvalidValueException("Số lượng tối đa phải >=0");
        }
        //Kiểm tra id có tồn tại không
        Event event2 = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại sự kiện với id: "+id));
        //Viết logger
        String content = "- Cập nhật sự kiện \""+event2.getEventName()+"\" thành \""+event.getEventName()+"\"";
        addLogger(userId, content);
        event2.setDescription(event.getDescription());
        event2.setEndDateTime(event.getEndDateTime());
        event2.setStartDateTime(event.getStartDateTime());
        event2.setEventName(event.getEventName());
        event2.setImg(event.getImg());
        event2.setTotalAttended(event.getTotalAttended());
        event2.setTotalRegistered(event.getTotalRegistered());
        event2.setMaxQuantity(event.getMaxQuantity());
        event2.setStatus(event.getStatus());
        event2.setOrganizer(o);
        eventRepository.save(event2);
        
    }
}
