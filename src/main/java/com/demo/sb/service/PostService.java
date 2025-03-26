package com.demo.sb.service;


import com.demo.sb.entity.Post;
import com.demo.sb.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Transactional
    public Post createPost(Post post) {
        post.setCreated(LocalDateTime.now());
        return postRepository.save(post);
    }

    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }

    public List<Post> getPostsByAuthor(int authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    @Transactional
    public void deletePost(int id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
        } else {
            throw new RuntimeException("Post not found");
        }
    }
}