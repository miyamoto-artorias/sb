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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class CardService {
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);
    
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

        // Generate a random 4-digit card number
        Random random = new Random();
        card.setCardNumber(String.format("%04d", random.nextInt(10000)));
        
        return cardRepository.save(card);
    }
    
    public Card getCardByUserId(int userId) {
        logger.info("Fetching card for user ID: {}", userId);
        return cardRepository.findByUser_Id(userId)
                .orElseThrow(() -> {
                    logger.error("Card not found for user ID: {}", userId);
                    return new EntityNotFoundException("Card not found for user ID: " + userId);
                });
    }

    public Card getCard(int id) {
        logger.info("Fetching card with ID: {}", id);
        return cardRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Card not found with ID: {}", id);
                    return new EntityNotFoundException("Card not found with ID: " + id);
                });
    }

    @Transactional
    public Card updateCardBalance(int cardId, float amount) {
        logger.info("Updating balance for card ID: {} by amount: {}", cardId, amount);
        
        Card card = getCard(cardId);
        float oldBalance = card.getBalance();
        float newBalance = oldBalance + amount;
        
        // Ensure balance doesn't go negative
        if (newBalance < 0) {
            logger.error("Cannot update balance to negative value. Card ID: {}, Current: {}, Change: {}", 
                    cardId, oldBalance, amount);
            throw new IllegalArgumentException("Card balance cannot be negative");
        }
        
        card.setBalance(newBalance);
        Card updatedCard = cardRepository.save(card);
        logger.info("Updated card ID: {} balance from {} to {}", cardId, oldBalance, updatedCard.getBalance());
        
        return updatedCard;
    }

    public Card getCardByCardNumber(String cardNumber) {
        logger.info("Fetching card with card number: {}", cardNumber);
        return cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> {
                    logger.error("Card not found with card number: {}", cardNumber);
                    return new EntityNotFoundException("Card not found with card number: " + cardNumber);
                });
    }

    @Transactional
    public Card updateCardBalanceByCardNumber(String cardNumber, float amount) {
        logger.info("Updating balance for card number: {} by amount: {}", cardNumber, amount);
        Card card = getCardByCardNumber(cardNumber);
        float oldBalance = card.getBalance();
        float newBalance = oldBalance + amount;
        
        if (newBalance < 0) {
            logger.error("Cannot update balance to negative value. Card number: {}, Current: {}, Change: {}", 
                    cardNumber, oldBalance, amount);
            throw new IllegalArgumentException("Card balance cannot be negative");
        }
        
        card.setBalance(newBalance);
        Card updatedCard = cardRepository.save(card);
        logger.info("Updated card number: {} balance from {} to {}", cardNumber, oldBalance, updatedCard.getBalance());
        
        return updatedCard;
    }

    @Transactional
    public Card updateCardUserId(int cardId, int newUserId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with ID: " + cardId));

        User newUser = userRepository.findById(newUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + newUserId));

        card.setUser(newUser);
        return cardRepository.save(card);
    }
}