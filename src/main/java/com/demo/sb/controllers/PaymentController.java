package com.demo.sb.controllers;

import com.demo.sb.entity.Payment;
import com.demo.sb.entity.PaymentRequest;
import com.demo.sb.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.demo.sb.dto.PaymentByUserRequest;
import com.demo.sb.dto.PaymentResponseDto;
import com.demo.sb.dto.PaymentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    
    @Autowired
    private PaymentService paymentService;

    // Updated to use PaymentDTO for response
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest paymentRequest) {
        logger.info("Received payment request: {}", paymentRequest);
        try {
            Payment savedPayment = paymentService.createPayment(paymentRequest);
            PaymentDTO paymentDTO = PaymentDTO.fromEntity(savedPayment);
            logger.info("Payment processed successfully: {}", paymentDTO.getId());
            return ResponseEntity.ok(paymentDTO);
        } catch (EntityNotFoundException ex) {
            logger.error("Entity not found: {}", ex.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            logger.error("Invalid argument: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error processing payment: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error processing payment: " + ex.getMessage());
        }
    }

    /*
    @PostMapping(
            consumes = {"application/json", "application/json;charset=UTF-8"},
            produces = "application/json")
    public ResponseEntity<?> createPayment(@RequestBody Payment payment) {
        try {
            Payment savedPayment = paymentService.createPayment(payment);
            if ("failed".equals(savedPayment.getStatus())) {
                return ResponseEntity.unprocessableEntity().body(savedPayment);
            }
            return ResponseEntity.ok(savedPayment);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    */

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable int id) {
        try {
            Optional<Payment> payment = paymentService.findById(id);
            if (payment.isPresent()) {
                PaymentDTO paymentDTO = PaymentDTO.fromEntity(payment.get());
                return ResponseEntity.ok(paymentDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("Error retrieving payment: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error retrieving payment");
        }
    }

    @PostMapping("/byUser")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByUser(@RequestBody @Valid PaymentByUserRequest request) {
        List<Payment> payments = paymentService.getPaymentsByUser(request.getUserId());
        List<PaymentResponseDto> responseDtos = payments.stream()
                .map(PaymentResponseDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }
}