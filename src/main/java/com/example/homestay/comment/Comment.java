package com.example.homestay.comment;

import java.time.LocalDateTime;

import com.example.homestay.homestayroom.Room;
import com.example.homestay.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "Vui lòng đăng nhập")
    private User user;
    private LocalDateTime localDateTime = LocalDateTime.now();
    @NotBlank(message = "Vui lòng nhập nội dung bình luận")
    private String content;
    @ManyToOne
    @JoinColumn(name = "room_id")
    @NotNull(message = "Vui lòng chọn homestay cần bình luận")
    private Room room;
    @ManyToOne(optional = true)
    @JoinColumn(name = "parent_comment_id")
    private Comment pComment;
}
