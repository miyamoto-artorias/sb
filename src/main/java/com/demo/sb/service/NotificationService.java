package com.demo.sb.service;


import com.demo.sb.entity.Notification;
import com.demo.sb.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(Notification notification) {
        notification.setCreated(LocalDateTime.now());
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    public Optional<Notification> findById(int id) {
        return notificationRepository.findById(id);
    }

    public List<Notification> getNotificationsByReceiver(int receiverId) {
        return notificationRepository.findByReceiverId(receiverId);
    }

    @Transactional
    public void markAsRead(int id) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent()) {
            notification.get().setRead(true);
            notificationRepository.save(notification.get());
        } else {
            throw new RuntimeException("Notification not found");
        }
    }
}