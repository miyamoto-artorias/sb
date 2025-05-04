package com.demo.sb.service;


import com.demo.sb.entity.Card;
import com.demo.sb.entity.User;
import com.demo.sb.repository.CardRepository;
import com.demo.sb.repository.UserRepository;
import com.demo.sb.dto.CardRequestDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class CardService {
    @Autowired private CardRepository cardRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public Card createCard(CardRequestDto cardDto) {
        User user = userRepository.findById(cardDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + cardDto.getUserId()));
        
        if (cardRepository.existsByUser_Id(cardDto.getUserId())) {
            throw new IllegalStateException("User already has a card associated.");
        }
        
        Card card = new Card();
        card.setPassword(cardDto.getPassword());
        card.setValid(cardDto.isValid());
        card.setBalance(cardDto.getBalance());
        card.setUser(user);
        card.setCardNumber(UUID.randomUUID().toString()); // Generate a unique card number
        
        return cardRepository.save(card);
    }
    public Card getCardByUserId(int userId) {
        return cardRepository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found for user ID: " + userId));
    }


    public Card getCard(int id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));
    }

    @Transactional
    public Card updateCardBalance(int cardId, float amount) {
        Card card = getCard(cardId);
        card.setBalance(card.getBalance() + amount);
        return cardRepository.save(card);
    }

    public Card getCardByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with card number: " + cardNumber));
    }

    @Transactional
    public Card updateCardBalanceByCardNumber(String cardNumber, float amount) {
        Card card = getCardByCardNumber(cardNumber);
        card.setBalance(card.getBalance() + amount);
        return cardRepository.save(card);
    }
}