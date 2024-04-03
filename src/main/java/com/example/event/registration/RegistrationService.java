package com.example.event.registration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.event.email.Email;
import com.example.event.email.EmailService;
import com.example.event.event.Event;
import com.example.event.event.EventRepository;
import com.example.event.event.EventStatus;
import com.example.event.exception.AlreadyExistsException;
import com.example.event.exception.InvalidValueException;
import com.example.event.exception.NotFoundException;
import com.example.event.logger.LoggerService;
import com.example.event.organizer.Organizer;
import com.example.event.organizer.OrganizerRepository;
import com.example.event.role.Role;
import com.example.event.user.User;
import com.example.event.user.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RegistrationService {
    private RegistrationRepository registrationRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private LoggerService loggerService;
    private EmailService emailService;
    private OrganizerRepository organizerRepository;
    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository, EventRepository eventRepository,
            UserRepository userRepository, LoggerService loggerService, EmailService emailService,OrganizerRepository organizerRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.loggerService = loggerService;
        this.emailService = emailService;
        this.organizerRepository = organizerRepository;
    }

    // Phương thức kiểm tra event có hợp lệ không
    private boolean isValidEvent(Integer eventId) {
        if(eventRepository.findById(eventId).isPresent()) {
            return true;
        }return false;
    }

    // Phương thức kiểm tra user có hợp lệ không
    private boolean isValidUser(Integer userId) {
        // Viết code kiểm tra xem user có tồn tại trong cơ sở dữ liệu không
        if(userRepository.findById(userId).isPresent()) {
            return true;
        }return false;
    }

    // Phương thức kiểm tra trạng thái của event
    private boolean isEventActive(Integer eventId) {
        // Viết code kiểm tra xem trạng thái của event có đang là true không
        if(eventRepository.findById(eventId).isPresent()) {
            if(eventRepository.findById(eventId).get().getStatus() == true) {
                return true;
            }
        }return false;
    }

    // Phương thức kiểm tra xem user đã đăng ký sự kiện khác trong khoảng thời gian này chưa
    private Integer isRegisteredInOtherEvent(Integer userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return registrationRepository.existsRegistrationWithSameDate(userId, startDateTime, endDateTime);
    }

    // Phương thức kiểm tra xem event còn chỗ trống không
    private boolean hasRemainingCapacity(Integer eventId) {
        // Viết code kiểm tra xem event còn chỗ trống không
        if(eventRepository.findById(eventId).isPresent()) {
            if(eventRepository.findById(eventId).get().getTotalRegistered()< eventRepository.findById(eventId).get().getMaxQuantity()) {
                return true;
            }
        }return false;
    }
    //Phương thức kiểm tra xem event và user đã tồn tại trước đó chưa
    public boolean isCheckEventIdAndUserId(Integer userId, Integer eventId) {
        return registrationRepository.existsByUsersIdAndEventId(userId, eventId);
    }

    // Phương thức thực hiện đăng ký người dùng cho sự kiện
    @Transactional
    public void registerUserForEvent(Registration registration) {
        //Lấy giá trị userId và eventId
        Integer userId = registration.getUsers().getId();
        Integer eventId = registration.getEvent().getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Không tồn tại user với id: "+registration.getUsers().getId()));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Không tồn tại sự kiện với id: "+registration.getEvent().getId()));
        Organizer organizer = organizerRepository.findById(event.getOrganizer().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại nhà tổ chức này"));
        // Viết code thực hiện đăng ký người dùng cho sự kiện
        if(!user.getStatus()) {
            throw new InvalidValueException("Không đăng ký được sự kiện. Tài khoản của bạn đã bị khóa");
        }
        if(isCheckEventIdAndUserId(userId, eventId)) {
            throw new AlreadyExistsException("Bạn đã đăng ký sự kiện này trước đó rồi");
        }
        if(!isEventActive(eventId)) {
            throw new InvalidValueException("Sự kiện này đã bị đóng");
        }
        if(!registration.getRegistrationDate().isBefore(event.getStartDateTime().toLocalDate())) {
             // Trừ đi một ngày
             System.out.println(event.getStartDateTime().toLocalDate().minusDays(1));
            LocalDateTime ngayTruoc = event.getStartDateTime().minusDays(1);
            // Lấy ngày, tháng và năm từ đối tượng LocalDateTime sau khi trừ đi
            int ngay = ngayTruoc.getDayOfMonth();
            int thang = ngayTruoc.getMonthValue();
            int nam = ngayTruoc.getYear();
            throw new InvalidValueException("Hạn cuối đăng ký sự kiện này là ngày: "+ngay+"/"+thang+"/"+nam+". Đã hết hạn đăng ký");
        }
        if(isRegisteredInOtherEvent(userId,event.getStartDateTime(),event.getEndDateTime())>0) {
            throw new AlreadyExistsException("Khoảng thời gian này bạn đã đăng ký sự kiện rồi");
        }
        if(!hasRemainingCapacity(eventId)) {
            throw new AlreadyExistsException("Sự kiện này đã đủ số lượng rồi");
        }
        event.setTotalRegistered(event.getTotalRegistered()+1);
        eventRepository.save(event);
        registrationRepository.save(registration);
        Email email = new Email();
        email.setSubject("THÔNG BÁO ĐĂNG KÝ SỰ KIỆN THÀNH CÔNG");
        email.setToEmail(user.getEmail());
        emailService.sendEmail(email, user, event, organizer);
        
    }
    public List<Event> getEventByUserId( Integer userId,
     EventStatus eventStatus,
       Integer organizerId,
        String eventName) {
            List<Registration> registrations = registrationRepository.findByUsersId(userId);
            List<Event> events = new ArrayList<>();
            for (Registration r : registrations) {
                Event e = eventRepository.findById(r.getEvent().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại event với id: "+r.getEvent().getId()));
                events.add(e);
            }
            LocalDateTime now = LocalDateTime.now();
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
    public Registration getRegistrationByUserIdAndEventId(Integer userId,Integer eventId) {
        return registrationRepository.findByUsersIdAndEventId(userId, eventId);
    }
    public List<Registration> getAllRegistrationByFilter(
        Integer eventId,
       String userFullname
    ) {
        //Lấy danh sách tổng ra
       List<Registration> registrations = registrationRepository.findAll();
       //Lấy user theo userFullname
       if(userFullname!=null) {
            registrations = registrationRepository.findByUsers_FullnameContainingOrUsers_UsernameContaining(userFullname, userFullname);
            
       }
       if(eventId!=null) {
            registrations.removeIf(r -> r.getEvent().getId()!=eventId);
       }
       return registrations;
       
    }
    private List<RegistrationResponseDTO> convertListRegistrationToListRegistrationResponseDTO(List<Registration> registrations) {
        List<RegistrationResponseDTO> registrationResponseDTOs = new ArrayList<>();
        for (Registration r : registrations) {
            Event e = eventRepository.findById(r.getEvent().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại sự kiện"));
            User u = userRepository.findById(r.getUsers().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại người dùng"));
            RegistrationResponseDTO registrationResponseDTO = new RegistrationResponseDTO();
            registrationResponseDTO.setId(r.getId());
            registrationResponseDTO.setEventName(e.getEventName());
            registrationResponseDTO.setStartDateTime(e.getStartDateTime());
            registrationResponseDTO.setEndDateTime(e.getEndDateTime());
            registrationResponseDTO.setImg(e.getImg());
            registrationResponseDTO.setFullname(u.getFullname());
            registrationResponseDTO.setAddress(u.getAddress());
            registrationResponseDTO.setPhoneNumber(u.getPhoneNumber());
            registrationResponseDTO.setEmail(u.getEmail());
            registrationResponseDTO.setRegistrationDate(r.getRegistrationDate());
            registrationResponseDTO.setStatus(r.getStatus());
        }
        return registrationResponseDTOs;
    }
    public void updateById(Integer id,Integer userId,Registration registration) {
        Registration r = registrationRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại bản đăng ký này"));
        User user = userRepository.findById(r.getUsers().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại người dùng đăng ký bản này"));
        Event e = eventRepository.findById(r.getEvent().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại sự kiện này"));
        r.setStatus(registration.getStatus());
        String message = r.getStatus() != 0 ? (r.getStatus() == 1 ? "Đã xác nhận tham gia" : "Không tham gia đúng hạn") : "Chưa xử lý"; 
        addLogger(userId, "- Cập nhật trạng thái đăng ký sự kiện \""+e.getEventName()+"\" của tài khoản \""+user.getUsername()+"\" thành \""+message+"\"");
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
