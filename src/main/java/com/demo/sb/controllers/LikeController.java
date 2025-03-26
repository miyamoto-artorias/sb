package com.demo.sb.controllers;


import com.demo.sb.entity.Like;
import com.demo.sb.service.LikeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/likes")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PostMapping
    public ResponseEntity<Like> createLike(@Valid @RequestBody Like like) {
        Like savedLike = likeService.createLike(like); // Assume this method exists
        return ResponseEntity.ok(savedLike);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Like> getLikeById(@PathVariable int id) {
        Optional<Like> like = likeService.findById(id); // Assume this method exists
        return like.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable int id) {
        likeService.deleteLike(id); // Assume this method exists
        return ResponseEntity.ok().build();
    }
}