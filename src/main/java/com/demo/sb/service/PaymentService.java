package com.demo.sb.service;

import com.demo.sb.entity.Card;
import com.demo.sb.entity.Payment;
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
        if (payment.getCard() == null) {
            throw new IllegalArgumentException("Payment must be associated with a card");
        }

        int cardId = payment.getCard().getId();
        Card dbCard = cardService.getCard(cardId);

        if (dbCard.getBalance() < payment.getAmount()) {
            payment.setStatus("failed");
            payment.setDate(LocalDateTime.now());
            payment.setCard(dbCard);
            return paymentRepository.save(payment);
        }

        dbCard.setBalance(dbCard.getBalance() - payment.getAmount());
        payment.setStatus("completed");
        payment.setDate(LocalDateTime.now());
        payment.setCard(dbCard);

        return paymentRepository.save(payment);
    }

    public Optional<Payment> findById(int id) {
        return paymentRepository.findById(id);
    }
}