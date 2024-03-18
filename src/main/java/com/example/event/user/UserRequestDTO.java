package com.example.event.user;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    @NotBlank(message = "Tài khoản không được để trống")
    private String username;
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
    @NotBlank(message = "Họ tên không được để trống")
    private String fullname;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    private List<String> roleNames = new ArrayList<>();
}
