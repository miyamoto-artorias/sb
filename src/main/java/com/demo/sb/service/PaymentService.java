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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CardService cardService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private EnrollmentService enrollmentService;

    @Transactional
    public Payment createPayment(PaymentRequest paymentRequest) {
        logger.info("Processing payment request: {}", paymentRequest);
        
        // Validate payment request
        if (paymentRequest == null) {
            logger.error("Payment request is null");
            throw new IllegalArgumentException("Payment request cannot be null");
        }
        
        // Fetch required entities with detailed error handling
        User payer = null;
        try {
            payer = userRepository.findById(paymentRequest.getPayerId())
                .orElseThrow(() -> new EntityNotFoundException("Payer not found with ID: " + paymentRequest.getPayerId()));
            logger.info("Found payer: {}", payer.getId());
        } catch (Exception e) {
            logger.error("Error finding payer with ID {}: {}", paymentRequest.getPayerId(), e.getMessage());
            throw new EntityNotFoundException("Payer not found with ID: " + paymentRequest.getPayerId());
        }

        User receiver = null;
        try {
            receiver = userRepository.findById(paymentRequest.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found with ID: " + paymentRequest.getReceiverId()));
            logger.info("Found receiver: {}", receiver.getId());
        } catch (Exception e) {
            logger.error("Error finding receiver with ID {}: {}", paymentRequest.getReceiverId(), e.getMessage());
            throw new EntityNotFoundException("Receiver not found with ID: " + paymentRequest.getReceiverId());
        }

        Course course = null;
        try {
            course = courseRepository.findById(paymentRequest.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + paymentRequest.getCourseId()));
            logger.info("Found course: {}", course.getId());
        } catch (Exception e) {
            logger.error("Error finding course with ID {}: {}", paymentRequest.getCourseId(), e.getMessage());
            throw new EntityNotFoundException("Course not found with ID: " + paymentRequest.getCourseId());
        }

        Card payerCard = null;
        try {
            payerCard = cardService.getCard(paymentRequest.getCardId());
            logger.info("Found payer card: {}", payerCard.getId());
        } catch (Exception e) {
            logger.error("Error finding card with ID {}: {}", paymentRequest.getCardId(), e.getMessage());
            throw new EntityNotFoundException("Card not found with ID: " + paymentRequest.getCardId());
        }

        // Check for existing COMPLETED payment
        try {
            if (paymentRepository.existsByPayerIdAndCourseIdAndStatus(payer.getId(), course.getId(), "completed")) {
                logger.info("Course already purchased by the user");
                throw new IllegalArgumentException("This course has already been purchased by the user");
            }
        } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
                logger.error("Error checking existing payment: {}", e.getMessage());
                throw new RuntimeException("Error checking existing payment", e);
            } else {
                throw e;
            }
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
            logger.info("Insufficient balance for payment. Required: {}, Available: {}", 
                    paymentRequest.getAmount(), payerCard.getBalance());
            payment.setStatus("failed");
            return paymentRepository.save(payment);
        }

        // Process payment
        try {
            Card receiverCard = cardService.getCardByUserId(receiver.getId());
            cardService.updateCardBalance(payerCard.getId(), -paymentRequest.getAmount());
            cardService.updateCardBalance(receiverCard.getId(), paymentRequest.getAmount());
            
            // After successful payment, create enrollment
            try {
                // Only attempt to create enrollment if payment is successful
                enrollmentService.createEnrollment(payer.getId(), course.getId());
                logger.info("Successfully created enrollment for user {} in course {}", 
                        payer.getId(), course.getId());
            } catch (IllegalArgumentException e) {
                // User is already enrolled, which is fine
                logger.info("User is already enrolled in this course: {}", e.getMessage());
            } catch (Exception e) {
                // Log other enrollment errors but don't fail the payment
                logger.error("Error creating enrollment: {}", e.getMessage());
            }
            
            payment.setStatus("completed");
            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Payment completed successfully with ID: {}", savedPayment.getId());
            return savedPayment;
        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage());
            payment.setStatus("failed");
            return paymentRepository.save(payment);
        }
    }

    public Optional<Payment> findById(int id) {
        return paymentRepository.findById(id);
    }

    // Add method to fetch payments by payer (user) ID
    public List<Payment> getPaymentsByUser(int userId) {
        return paymentRepository.findByPayerId(userId);
    }
}