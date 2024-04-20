package com.example.homestay.homestayroom;

import java.util.List;

import com.example.homestay.homestaytype.Type;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Tên homestay không được để trống")
    private String roomName;
    private String description;
    @NotNull(message = "Số lượng không được để trống")
    private Integer maxQuantity;
    private Boolean status = true;
    @NotBlank(message = "Hình ảnh homestay không được để trống")
    private String img;
    private String listDevices;
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;
}
