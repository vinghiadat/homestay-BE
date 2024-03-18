package com.example.event.registration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.event.event.Event;
import com.example.event.event.EventRepository;
import com.example.event.exception.AlreadyExistsException;
import com.example.event.exception.InvalidValueException;
import com.example.event.exception.NotFoundException;
import com.example.event.user.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RegistrationService {
    private RegistrationRepository registrationRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository, EventRepository eventRepository,
            UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
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
    private boolean isRegisteredInOtherEvent(Integer eventId,Integer userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return registrationRepository.existsRegistrationWithSameDate(userId, eventId, startDateTime, endDateTime);
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
    private boolean isCheckEventIdAndUserId(Integer userId, Integer eventId) {
        return registrationRepository.existsByUsersIdAndEventId(userId, eventId);
    }

    // Phương thức thực hiện đăng ký người dùng cho sự kiện
    @Transactional
    public void registerUserForEvent(Registration registration) {
        // Viết code thực hiện đăng ký người dùng cho sự kiện
        if(!isValidEvent(registration.getEvent().getId())) {
            throw new NotFoundException("Không tồn tại sự kiện với id: "+registration.getEvent().getId());
        }
        if(!isValidUser(registration.getUsers().getId())) {
            throw new NotFoundException("Không tồn tại user với id: "+registration.getUsers().getId());
        }
       
        if(isCheckEventIdAndUserId(registration.getUsers().getId(), registration.getEvent().getId())) {
            throw new AlreadyExistsException("Bạn đã đăng ký sự kiện này trước đó rồi");
        }
        if(!isEventActive(registration.getEvent().getId())) {
            throw new InvalidValueException("Sự kiện này đã bị đóng");
        }
        if(isRegisteredInOtherEvent(registration.getUsers().getId(),registration.getEvent().getId(),registration.getEvent().getStartDateTime(),registration.getEvent().getEndDateTime())) {
            throw new AlreadyExistsException("Khoảng thời gian này bạn đã đăng ký sự kiện rồi");
        }
        if(!hasRemainingCapacity(registration.getEvent().getId())) {
            throw new AlreadyExistsException("Sự kiện này đã đủ số lượng rồi");
        }
        Optional<Event> event = eventRepository.findById(registration.getEvent().getId());
        if(event.isPresent()) {
            event.get().setTotalRegistered(event.get().getTotalRegistered()+1);
            eventRepository.save(event.get());
        }
        registrationRepository.save(registration);
        
    }
}
