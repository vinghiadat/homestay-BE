package com.example.homestay.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.homestay.homestayroom.Room;
import com.example.homestay.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @NotNull(message = "Vui lòng chọn phòng")
    private Room room;
    private LocalDate registrationDate = LocalDate.now();
    @NotNull(message = "Vui lòng nhập ngày bắt đầu")
    private LocalDateTime startDateTime;
    @NotNull(message = "Vui lòng nhập ngày kết thúc")
    private LocalDateTime endDateTime;
    private Float price;
    /*
     * 0 là chưa xử lý
     * -1 đã hủy
     * 1 xác nhận
     * 2 đã thanh toán
     */
    private Integer status = 0;
    private String paymentMethod;
    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id")
    private User users;
    private String fullname;
    private String phoneNumber;
    private String email;
}
