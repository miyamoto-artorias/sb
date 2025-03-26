package com.demo.sb.controllers;


import com.demo.sb.entity.Payment;
import com.demo.sb.service.PaymentService;
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

    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        Payment savedPayment = paymentService.createPayment(payment); // Assume this method exists
        return ResponseEntity.ok(savedPayment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable int id) {
        Optional<Payment> payment = paymentService.findById(id); // Assume this method exists
        return payment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}