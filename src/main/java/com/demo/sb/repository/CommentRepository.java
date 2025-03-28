package com.demo.sb.repository;


import com.demo.sb.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostId(int postId);
    List<Comment> findByParentId(int parentId);

}