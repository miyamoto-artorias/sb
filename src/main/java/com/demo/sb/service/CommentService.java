package com.demo.sb.service;


import com.demo.sb.entity.Category;
import com.demo.sb.entity.Comment;
import com.demo.sb.entity.Post;
import com.demo.sb.entity.User;
import com.demo.sb.repository.CommentRepository;
import com.demo.sb.repository.PostRepository;
import com.demo.sb.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired private CommentRepository commentRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public Comment createComment(Comment comment, int postId, int authorId, Integer parentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found"));
            comment.setParent(parent);
        }
        return commentRepository.save(comment);
    }

    public Comment findById(int id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with ID " + id + " not found"));
    }
    // New method to delete a comment
    @Transactional
    public void deleteComment(int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with ID " + commentId + " not found"));
        commentRepository.delete(comment);
    }


}