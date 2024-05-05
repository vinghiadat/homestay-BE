package com.example.homestay.comment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.homestay.exception.InvalidValueException;
import com.example.homestay.exception.NotFoundException;
import com.example.homestay.homestayroom.RoomRepository;
import com.example.homestay.user.User;
import com.example.homestay.user.UserRepository;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;
    public List<Comment> getCommentsByRoomId(Integer roomId) {
        return commentRepository.findByRoomId(roomId);
    }
    public void addComment(Comment comment) {
        userRepository.findById(comment.getUser().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại user này"));
        roomRepository.findById(comment.getRoom().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại homestay này"));
        if(comment.getPComment()!=null ) {
            commentRepository.findById(comment.getPComment().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại bình luận này trước đó"));
        }
        commentRepository.save(comment);
    }
    public void deleteCommentById(Integer id, Integer userId) {
        //Kiểm tra Comment này có phải của user này không
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại binh luận này"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Không tồn tại người dùng này"));
        if(user.getRoles().contains("ADMIN")) {
            
        }else {
            if(!comment.getUser().getId().equals(userId)) {
                throw new InvalidValueException("Bình luận này bạn không có quyền xóa");
            }
        }
        
        commentRepository.deleteById(id);
    }
}
