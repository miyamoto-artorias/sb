package com.demo.sb.repository;

import com.demo.sb.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
    @Query("SELECT a FROM QuizAttempt a WHERE a.user.id = :userId AND a.quiz.quizId = :quizId")
    List<QuizAttempt> findByUserIdAndQuizId(@Param("userId") int userId, @Param("quizId") Long quizId);
    
    @Query("SELECT a FROM QuizAttempt a WHERE a.quiz.quizId = :quizId")
    List<QuizAttempt> findByQuizId(@Param("quizId") Long quizId);
} 