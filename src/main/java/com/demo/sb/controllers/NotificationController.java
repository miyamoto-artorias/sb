package com.demo.sb.controllers;


import com.demo.sb.entity.Notification;
import com.demo.sb.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody Notification notification) {
        Notification savedNotification = notificationService.createNotification(notification); // Assume this method exists
        return ResponseEntity.ok(savedNotification);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable int id) {
        Optional<Notification> notification = notificationService.findById(id); // Assume this method exists
        return notification.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<Notification>> getNotificationsByReceiver(@PathVariable int receiverId) {
        List<Notification> notifications = notificationService.getNotificationsByReceiver(receiverId); // Assume this method exists
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable int id) {
        notificationService.markAsRead(id); // Assume this method exists
        return ResponseEntity.ok().build();
    }
}