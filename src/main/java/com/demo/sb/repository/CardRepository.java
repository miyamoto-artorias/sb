package com.demo.sb.repository;


import com.demo.sb.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Integer> {
    Card findByUserId(int userId);
}