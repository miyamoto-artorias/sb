package com.demo.sb.repository;


import com.demo.sb.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByReceiverId(int receiverId);

    /*
    findBy: Indicates a query method that retrieves entities.
    ReceiverId: Refers to a property path in the Notification entity.
    Receiver: Matches the receiver field in Notification (a User entity).
    Id: Refers to the id field of the User entity (since receiver is a User).
    * */
}