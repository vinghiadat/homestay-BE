package com.example.event.registration;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegistrationResponseDTO {
    private Integer id;
    private String eventName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String img;
    private String fullname;
    private String address;
    private String phoneNumber;
    private String email;
    private LocalDate registrationDate;
    private Integer status = 1;
}
