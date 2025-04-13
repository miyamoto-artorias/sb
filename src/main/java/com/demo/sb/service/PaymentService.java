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
        // Validate payment
        if (payment.getCard() == null || payment.getReceiver() == null) {
            throw new IllegalArgumentException("Payment must have a card and receiver");
        }

        // Get payer's card
        Card payerCard = cardService.getCard(payment.getCard().getId());

        // Get receiver's card from user
        User receiver = payment.getReceiver();
        Card receiverCard = cardService.getCardByUserId(receiver.getId()); // Fetch card by user ID

        // Check payer balance
        if (payerCard.getBalance() < payment.getAmount()) {
            payment.setStatus("failed");
            payment.setDate(LocalDateTime.now());
            return paymentRepository.save(payment);
        }

        // Perform balance transfers
        cardService.updateCardBalance(payerCard.getId(), -payment.getAmount());
        cardService.updateCardBalance(receiverCard.getId(), payment.getAmount());

        // Update payment status
        payment.setStatus("completed");
        payment.setDate(LocalDateTime.now());
        payment.setCard(payerCard); // Ensure full card object is persisted

        return paymentRepository.save(payment);
    }

    public Optional<Payment> findById(int id) {
        return paymentRepository.findById(id);
    }
}