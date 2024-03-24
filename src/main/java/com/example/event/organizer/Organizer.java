package com.example.event.organizer;

import java.util.List;

import com.example.event.event.Event;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organizer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Vui lòng nhập tên nhà tổ chức")
    private String organizerName;
    @NotBlank(message = "Vui lòng nhập email")
    @Email(message = "Vui lòng nhập đúng định dạng email")
    private String email;
    @NotBlank(message = "Vui lòng nhập số điện thoại")
    private String phone;
    @NotBlank(message = "Vui lòng nhập địa chỉ")
    private String address;
    @NotBlank(message = "Vui lòng nhập hình ảnh")
    private String img;
}