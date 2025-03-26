package com.demo.sb.controllers;


import com.demo.sb.entity.Comment;
import com.demo.sb.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> createComment(@Valid @RequestBody Comment comment) {
        Comment savedComment = commentService.createComment(comment); // Assume this method exists
        return ResponseEntity.ok(savedComment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable int id) {
        Optional<Comment> comment = commentService.findById(id); // Assume this method exists
        return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable int id) {
        commentService.deleteComment(id); // Assume this method exists
        return ResponseEntity.ok().build();
    }
}