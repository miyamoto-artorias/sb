package com.demo.sb.repository;


import com.demo.sb.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    List<Like> findByPostId(int postId);

}