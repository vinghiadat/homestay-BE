package com.example.homestay.comment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = {"http://localhost:4200"})
@RequestMapping("/api/v1/comment")
public class CommentResource {
    @Autowired
    private CommentService commentService;

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Comment>> getCommentsByRoomId(@PathVariable Integer roomId) {
        return ResponseEntity.ok().body(commentService.getCommentsByRoomId(roomId));
    }
    @PostMapping()
    public ResponseEntity<Void> addComment(@RequestBody @Valid Comment comment) {
        commentService.addComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> deleteCommentById(@PathVariable Integer id,@PathVariable Integer userId) {
        commentService.deleteCommentById(id,userId);
        return ResponseEntity.noContent().build();
    }

}
