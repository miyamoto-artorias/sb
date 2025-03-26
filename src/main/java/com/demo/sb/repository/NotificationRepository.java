package com.demo.sb.repository;


import com.demo.sb.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByReceiverId(int receiverId);
}