package com.demo.sb.service;

import com.demo.sb.entity.*;
import com.demo.sb.repository.CourseRepository;
import com.demo.sb.repository.PaymentRepository;
import com.demo.sb.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CardService cardService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;


    @Transactional
    public Payment createPayment(PaymentRequest paymentRequest) {
        // Fetch required entities
        User payer = userRepository.findById(paymentRequest.getPayerId())
                .orElseThrow(() -> new EntityNotFoundException("Payer not found with ID: " + paymentRequest.getPayerId()));

        User receiver = userRepository.findById(paymentRequest.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found with ID: " + paymentRequest.getReceiverId()));

        Course course = courseRepository.findById(paymentRequest.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + paymentRequest.getCourseId()));

        Card payerCard = cardService.getCard(paymentRequest.getCardId());

        // Check for existing COMPLETED payment
        if (paymentRepository.existsByPayerIdAndCourseIdAndStatus(payer.getId(), course.getId(), "completed")) {
            throw new IllegalArgumentException("This course has already been purchased by the user");
        }


        // Prepare payment entity
        Payment payment = new Payment();
        payment.setAmount(paymentRequest.getAmount());
        payment.setPayer(payer);
        payment.setReceiver(receiver);
        payment.setCourse(course);
        payment.setCard(payerCard);
        payment.setDate(LocalDateTime.now());

        // Check payer's balance
        if (payerCard.getBalance() < paymentRequest.getAmount()) {
            payment.setStatus("failed");
            return paymentRepository.save(payment);
        }

        // Process payment
        Card receiverCard = cardService.getCardByUserId(receiver.getId());
        cardService.updateCardBalance(payerCard.getId(), -paymentRequest.getAmount());
        cardService.updateCardBalance(receiverCard.getId(), paymentRequest.getAmount());

        payment.setStatus("completed");
        return paymentRepository.save(payment);
    }



    /*
    @Transactional
    public Payment createPayment(Payment payment) {
        // Validate input
        if (payment.getCard() == null || payment.getReceiver() == null || payment.getCourse() == null || payment.getPayer() == null) {
            throw new IllegalArgumentException("Payment must have card, receiver, payer, and course");
        }

        int payerId = payment.getPayer().getId();
        int courseId = payment.getCourse().getId();

        // Check if the payment already exists
        if (paymentRepository.existsByPayerIdAndCourseId(payerId, courseId)) {
            throw new IllegalArgumentException("This course has already been purchased by the user.");
        }

        Card payerCard = cardService.getCard(payment.getCard().getId());
        Card receiverCard = cardService.getCardByUserId(payment.getReceiver().getId());

        if (payerCard.getBalance() < payment.getAmount()) {
            payment.setStatus("failed");
            payment.setDate(LocalDateTime.now());
            return paymentRepository.save(payment);
        }

        cardService.updateCardBalance(payerCard.getId(), -payment.getAmount());
        cardService.updateCardBalance(receiverCard.getId(), payment.getAmount());

        payment.setStatus("completed");
        payment.setDate(LocalDateTime.now());
        payment.setCard(payerCard);

        return paymentRepository.save(payment);
    }
        */
    public Optional<Payment> findById(int id) {
        return paymentRepository.findById(id);
    }

    // Add method to fetch payments by payer (user) ID
    public List<Payment> getPaymentsByUser(int userId) {
        return paymentRepository.findByPayerId(userId);
    }
}