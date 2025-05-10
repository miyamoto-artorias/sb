package com.demo.sb.service;

import com.demo.sb.entity.*;
import com.demo.sb.repository.CourseRepository;
import com.demo.sb.repository.PaymentRepository;
import com.demo.sb.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    /**
     * Create a payment and process all related operations
     */
    @Transactional
    public Payment createPayment(PaymentRequest paymentRequest) {
        logger.info("Starting payment request processing: {}", paymentRequest);
        
        // Do all initial validation before starting any transactions
        if (paymentRequest == null) {
            logger.error("Payment request is null");
            throw new IllegalArgumentException("Payment request cannot be null");
        }
        
        // First, check if the course was already purchased
        boolean alreadyPurchased = checkIfAlreadyPurchased(paymentRequest.getPayerId(), paymentRequest.getCourseId());
        if (alreadyPurchased) {
            logger.info("Course {} already purchased by user {}", paymentRequest.getCourseId(), paymentRequest.getPayerId());
            throw new IllegalArgumentException("This course has already been purchased by the user");
        }
        
        // Load necessary entities
        User payer = loadPayer(paymentRequest.getPayerId());
        User receiver = loadReceiver(paymentRequest.getReceiverId());
        Course course = loadCourse(paymentRequest.getCourseId());
        Card payerCard = loadPayerCard(paymentRequest.getCardId());
        Card receiverCard = loadReceiverCard(receiver.getId());
        
        // Check if payer has enough balance
        boolean hasEnoughBalance = checkBalance(payerCard, paymentRequest.getAmount());
        if (!hasEnoughBalance) {
            logger.info("Insufficient balance. Creating failed payment record");
            return savePaymentWithStatus(payer, receiver, course, payerCard, paymentRequest.getAmount(), "failed");
        }
        
        // Process payment - this is a separate transaction to avoid issues
        boolean paymentSuccess = processPaymentTransfer(payerCard, receiverCard, paymentRequest.getAmount());
        if (!paymentSuccess) {
            logger.error("Payment processing failed");
            return savePaymentWithStatus(payer, receiver, course, payerCard, paymentRequest.getAmount(), "failed");
        }
        
        // Create enrollment - again in a separate transaction
        boolean enrollmentSuccess = createEnrollment(payer.getId(), course.getId());
        // Even if enrollment fails, we don't fail the payment
        
        // Save successful payment
        return savePaymentWithStatus(payer, receiver, course, payerCard, paymentRequest.getAmount(), "completed");
    }
    
    /**
     * Check if the course was already purchased by this user
     */
    @Transactional(readOnly = true)
    private boolean checkIfAlreadyPurchased(Integer payerId, Integer courseId) {
        try {
            return paymentRepository.existsByPayerIdAndCourseIdAndStatus(payerId, courseId, "completed");
        } catch (Exception e) {
            logger.error("Error checking existing payment: {}", e.getMessage());
            return false; // Assume not purchased in case of error
        }
    }
    
    /**
     * Load payer user entity
     */
    @Transactional(readOnly = true)
    private User loadPayer(Integer payerId) {
        try {
            User payer = userRepository.findById(payerId)
                .orElseThrow(() -> new EntityNotFoundException("Payer not found with ID: " + payerId));
            logger.info("Found payer: {}", payer.getId());
            return payer;
        } catch (Exception e) {
            logger.error("Error finding payer with ID {}: {}", payerId, e.getMessage());
            throw new RuntimeException("Could not load payer", e);
        }
    }
    
    /**
     * Load receiver user entity
     */
    @Transactional(readOnly = true)
    private User loadReceiver(Integer receiverId) {
        try {
            User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found with ID: " + receiverId));
            logger.info("Found receiver: {}", receiver.getId());
            return receiver;
        } catch (Exception e) {
            logger.error("Error finding receiver with ID {}: {}", receiverId, e.getMessage());
            throw new RuntimeException("Could not load receiver", e);
        }
    }
    
    /**
     * Load course entity
     */
    @Transactional(readOnly = true)
    private Course loadCourse(Integer courseId) {
        try {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
            logger.info("Found course: {}", course.getId());
            return course;
        } catch (Exception e) {
            logger.error("Error finding course with ID {}: {}", courseId, e.getMessage());
            throw new RuntimeException("Could not load course", e);
        }
    }
    
    /**
     * Load payer's card entity
     */
    @Transactional(readOnly = true)
    private Card loadPayerCard(Integer cardId) {
        try {
            Card card = cardService.getCard(cardId);
            logger.info("Found payer card: {} with balance: {}", card.getId(), card.getBalance());
            return card;
        } catch (Exception e) {
            logger.error("Error finding card with ID {}: {}", cardId, e.getMessage());
            throw new RuntimeException("Could not load payer card", e);
        }
    }
    
    /**
     * Load receiver's card entity
     */
    @Transactional(readOnly = true)
    private Card loadReceiverCard(Integer userId) {
        try {
            Card card = cardService.getCardByUserId(userId);
            logger.info("Found receiver card: {} with balance: {}", card.getId(), card.getBalance());
            return card;
        } catch (Exception e) {
            logger.error("Error finding receiver's card: {}", e.getMessage());
            throw new RuntimeException("Could not load receiver card: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if payer has enough balance
     */
    private boolean checkBalance(Card payerCard, float amount) {
        float availableBalance = payerCard.getBalance();
        logger.info("Checking balance - Required: {}, Available: {}", amount, availableBalance);
        return availableBalance >= amount;
    }
    
    /**
     * Process the actual payment transfer between cards
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean processPaymentTransfer(Card payerCard, Card receiverCard, float amount) {
        try {
            // Deduct from payer
            cardService.updateCardBalance(payerCard.getId(), -amount);
            logger.info("Successfully deducted {} from payer's card {}", amount, payerCard.getId());
            
            // Add to receiver
            cardService.updateCardBalance(receiverCard.getId(), amount);
            logger.info("Successfully added {} to receiver's card {}", amount, receiverCard.getId());
            
            return true;
        } catch (Exception e) {
            logger.error("Error during payment transfer: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Create enrollment with a separate transaction
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean createEnrollment(Integer userId, Integer courseId) {
        try {
            Enrollment enrollment = enrollmentService.createEnrollment(userId, courseId);
            logger.info("Successfully created enrollment ID: {} for user {} in course {}", 
                    enrollment.getId(), userId, courseId);
            return true;
        } catch (Exception e) {
            logger.error("Error creating enrollment: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Save payment with specific status
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Payment savePaymentWithStatus(User payer, User receiver, Course course, Card card, float amount, String status) {
        try {
            Payment payment = new Payment();
            payment.setPayer(payer);
            payment.setReceiver(receiver);
            payment.setCourse(course);
            payment.setCard(card);
            payment.setAmount(amount);
            payment.setDate(LocalDateTime.now());
            payment.setStatus(status);
            
            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Saved payment with ID: {} and status: {}", savedPayment.getId(), status);
            return savedPayment;
        } catch (Exception e) {
            logger.error("Error saving payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save payment record", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Payment> findById(int id) {
        return paymentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByUser(int userId) {
        return paymentRepository.findByPayerId(userId);
    }
}