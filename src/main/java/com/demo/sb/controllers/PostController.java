package com.demo.sb.controllers;


import com.demo.sb.entity.Post;
import com.demo.sb.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post) {
        Post savedPost = postService.createPost(post); // Assume this method exists
        return ResponseEntity.ok(savedPost);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable int id) {
        Optional<Post> post = postService.findById(id); // Assume this method exists
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Post>> getPostsByAuthor(@PathVariable int authorId) {
        List<Post> posts = postService.getPostsByAuthor(authorId); // Assume this method exists
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id) {
        postService.deletePost(id); // Assume this method exists
        return ResponseEntity.ok().build();
    }
}