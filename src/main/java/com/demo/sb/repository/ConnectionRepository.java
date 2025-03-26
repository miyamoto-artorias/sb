package com.demo.sb.repository;


import com.demo.sb.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Integer> {
    List<Connection> findByUser1IdOrUser2Id(int user1Id, int user2Id);
}