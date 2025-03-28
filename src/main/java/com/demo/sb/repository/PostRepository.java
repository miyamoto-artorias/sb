package com.demo.sb.repository;


import com.demo.sb.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByAuthorId(int authorId);

}