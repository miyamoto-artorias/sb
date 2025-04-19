package com.demo.sb.controllers;

import com.demo.sb.entity.Payment;
import com.demo.sb.entity.PaymentRequest;
import com.demo.sb.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;


    // In PaymentController.java
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            Payment savedPayment = paymentService.createPayment(paymentRequest);
            return ResponseEntity.ok(savedPayment);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
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
    public ResponseEntity<Payment> getPaymentById(@PathVariable int id) {
        Optional<Payment> payment = paymentService.findById(id);
        return payment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}