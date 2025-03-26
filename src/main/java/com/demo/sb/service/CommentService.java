package com.demo.sb.service;


import com.demo.sb.entity.Comment;
import com.demo.sb.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public Comment createComment(Comment comment) {
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public Optional<Comment> findById(int id) {
        return commentRepository.findById(id);
    }

    @Transactional
    public void deleteComment(int id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Comment not found");
        }
    }
}