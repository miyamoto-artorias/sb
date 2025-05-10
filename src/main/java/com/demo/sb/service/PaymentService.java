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
            logger.info("Found payer card: {} with balance: {}", payerCard.getId(), payerCard.getBalance());
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
        float requiredAmount = paymentRequest.getAmount();
        float availableBalance = payerCard.getBalance();
        
        logger.info("Checking balance - Required: {}, Available: {}", requiredAmount, availableBalance);
        
        if (availableBalance < requiredAmount) {
            logger.info("Insufficient balance for payment. Required: {}, Available: {}", 
                    requiredAmount, availableBalance);
            payment.setStatus("failed");
            return paymentRepository.save(payment);
        }

        // Process payment
        Card receiverCard = null;
        try {
            // Get receiver's card
            receiverCard = cardService.getCardByUserId(receiver.getId());
            logger.info("Found receiver card: {} with balance: {}", receiverCard.getId(), receiverCard.getBalance());
            
            // Update balances
            float deductAmount = -requiredAmount; // Negative for deduction
            cardService.updateCardBalance(payerCard.getId(), deductAmount);
            logger.info("Updated payer card balance: {}", payerCard.getBalance() + deductAmount);
            
            cardService.updateCardBalance(receiverCard.getId(), requiredAmount);
            logger.info("Updated receiver card balance: {}", receiverCard.getBalance() + requiredAmount);
            
            // Create enrollment after successful payment
            Enrollment enrollment = enrollmentService.createEnrollment(payer.getId(), course.getId());
            logger.info("Successfully created or found enrollment ID: {} for user {} in course {}", 
                    enrollment.getId(), payer.getId(), course.getId());
            
            // Set payment status to completed
            payment.setStatus("completed");
            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Payment completed successfully with ID: {}", savedPayment.getId());
            return savedPayment;
            
        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage(), e);
            // Rollback any balance changes if there was an error
            try {
                if (payerCard != null && receiverCard != null) {
                    // Try to restore the original balances
                    logger.info("Rolling back balance changes due to error");
                }
            } catch (Exception rollbackEx) {
                logger.error("Error rolling back balance changes: {}", rollbackEx.getMessage());
            }
            
            payment.setStatus("failed");
            Payment failedPayment = paymentRepository.save(payment);
            logger.error("Payment failed with ID: {}", failedPayment.getId());
            return failedPayment;
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