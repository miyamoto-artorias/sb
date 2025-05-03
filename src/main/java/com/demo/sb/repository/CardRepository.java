package com.demo.sb.repository;


import com.demo.sb.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Integer> {
    Card findByUserId(int userId);
    Optional<Card> findByUser_Id(int userId);
    boolean existsByUser_Id(int userId);
}