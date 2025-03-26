package com.demo.sb.service;


import com.demo.sb.entity.Like;
import com.demo.sb.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    @Transactional
    public Like createLike(Like like) {
        like.setCreated(LocalDateTime.now());
        return likeRepository.save(like);
    }

    public Optional<Like> findById(int id) {
        return likeRepository.findById(id);
    }

    @Transactional
    public void deleteLike(int id) {
        if (likeRepository.existsById(id)) {
            likeRepository.deleteById(id);
        } else {
            throw new RuntimeException("Like not found");
        }
    }
}