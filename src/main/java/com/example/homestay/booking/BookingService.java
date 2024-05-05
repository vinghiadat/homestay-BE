package com.example.homestay.booking;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.homestay.email.Email;
import com.example.homestay.email.EmailService;
import com.example.homestay.exception.InvalidValueException;
import com.example.homestay.exception.NotFoundException;
import com.example.homestay.homestayroom.Room;
import com.example.homestay.homestayroom.RoomRepository;
import com.example.homestay.homestayroom.RoomService;
import com.example.homestay.homestaytype.Type;
import com.example.homestay.homestaytype.TypeRepository;
import com.example.homestay.logger.LoggerService;
import com.example.homestay.role.Role;
import com.example.homestay.user.User;
import com.example.homestay.user.UserRepository;



@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private LoggerService loggerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomService roomService;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private EmailService emailService;
    public List<Booking> getAllBookings(LocalDate registrationDate,String username) {
        List<Booking> bookings = bookingRepository.findByOrderByRegistrationDateAsc();
        if(registrationDate!=null) {
            bookings.removeIf(b -> !b.getRegistrationDate().equals(registrationDate));
        }
        if(username!=null ) {
            bookings.removeIf(b -> !b.getUsers().getFullname().toLowerCase().contains(username.toLowerCase()));
        }
        Collections.reverse(bookings);
        return bookings;
    }
    public void addBooking(Booking booking) {
        User user  = new User();
        if(booking.getUsers()!=null) {
           user =  userRepository.findById(booking.getUsers().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại user với id: "+booking.getUsers().getId()));
        }
        Room room = roomRepository.findById(booking.getRoom().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại homestay với id: "+booking.getRoom().getId()));
        if(booking.getStartDateTime().isAfter(booking.getEndDateTime())) {
            throw new InvalidValueException("Ngày bắt đầu không được lớn hơn ngày kết thúc");
        } 
        if(roomService.isRoomBooked(room,booking.getStartDateTime(),booking.getEndDateTime())) {
            throw new InvalidValueException("Phòng đã được đặt trong thời gian này rồi");
        }
        Type type = typeRepository.findById(room.getType().getId()).orElseThrow(() -> new NotFoundException("Không có loại homestay phù hợp"));
        LocalDate startDate = booking.getStartDateTime().toLocalDate();
        LocalDate endDate = booking.getEndDateTime().toLocalDate();
        // Tính số ngày giữa hai LocalDate
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        booking.setPrice(room.getPrice()*daysBetween);
        booking.setPaymentMethod("Thanh toán trực tiếp");
        bookingRepository.save(booking);
        Email email = new Email();
        email.setSubject("THÔNG BÁO ĐẶT PHÒNG THÀNH CÔNG");
        email.setToEmail(user.getEmail());
        emailService.sendEmail(email, user, room, type , booking);
    }
    public void updateBookingById(Integer id, Integer userId, Booking b) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại đơn đặt homestay với id: "+id));
        Type type = typeRepository.findById(booking.getRoom().getType().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại loại homestay"));
        booking.setStatus(b.getStatus());
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Không tồn tại người dùng với id: "+userId));
        String tenKhachHang = booking.getUsers()!=null ? booking.getUsers().getFullname() : booking.getFullname();
        String trangThai = "";
        String subjectMail = new String();
        if(b.getStatus() == 0) {
            trangThai = "Hoàn tác";
            subjectMail = "THÔNG BÁO ĐƠN ĐẶT PHÒNG CỦA BẠN ĐÃ BỊ HOÀN TÁC";
        }
        if(b.getStatus() == 1) {
            trangThai = "Đã xác nhận";
            subjectMail = "THÔNG BÁO ĐƠN ĐẶT PHÒNG CỦA BẠN ĐÃ ĐƯỢC XÁC NHẬN";
        }
        if(b.getStatus() == 2) {
            trangThai = "Đã thanh toán";
            subjectMail = "THÔNG BÁO ĐƠN ĐẶT PHÒNG CỦA BẠN ĐÃ THANH TOÁN";
        }
        if(b.getStatus() == -1) {
            trangThai = "Đã hủy";
            subjectMail = "THÔNG BÁO ĐƠN ĐẶT PHÒNG CỦA BẠN ĐÃ BỊ HỦY";
        }
        addLogger(userId, "- Cập nhật trạng thái đặt homestay \""+booking.getRoom().getRoomName()+"\" của khách hàng "+tenKhachHang+" thành trạng thái \""+trangThai+"\"");
        bookingRepository.save(booking);
        System.out.println(booking.getUsers());
        Email email = new Email();
        email.setSubject(subjectMail);
        email.setToEmail(booking.getUsers().getEmail());
        emailService.sendEmail(email, booking.getUsers(), booking.getRoom(), type, booking);

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
    public List<Booking> getBookingsByUserId(Integer id,
        LocalDate registrationDate,
        Integer typeId
        ,String roomName
        ) {
            List<Booking> bookings = bookingRepository.findByOrderByRegistrationDateAsc();
            if(id != null ) {
                userRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại khách hàng với id: "+id));
                bookings.removeIf(b -> !b.getUsers().getId().equals(id));
            }
            if(registrationDate!=null) {
                bookings.removeIf(b -> !b.getRegistrationDate().equals(registrationDate));
            }
            if(typeId!=null ) {
                bookings.removeIf(b -> !b.getRoom().getType().getId().equals(typeId));
            }
            if(roomName!=null ) {
                bookings.removeIf(b -> !b.getRoom().getRoomName().toLowerCase().contains(roomName));
            }
            Collections.reverse(bookings);
            return bookings;
        }
}
