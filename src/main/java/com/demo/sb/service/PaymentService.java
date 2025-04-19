package com.demo.sb.service;

import com.demo.sb.entity.Card;
import com.demo.sb.entity.Payment;
import com.demo.sb.entity.User;
import com.demo.sb.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CardService cardService;

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

    public Optional<Payment> findById(int id) {
        return paymentRepository.findById(id);
    }
}