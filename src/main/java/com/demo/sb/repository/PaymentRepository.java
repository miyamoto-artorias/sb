package com.demo.sb.repository;


import com.demo.sb.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByPayerId(int payerId);
    List<Payment> findByReceiverId(int receiverId);
    boolean existsByPayerIdAndCourseIdAndStatus(int payerId, int courseId, String status);


}