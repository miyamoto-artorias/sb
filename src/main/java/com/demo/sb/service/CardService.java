package com.demo.sb.service;


import com.demo.sb.entity.Card;
import com.demo.sb.entity.User;
import com.demo.sb.repository.CardRepository;
import com.demo.sb.repository.UserRepository;
import com.demo.sb.dto.CardRequestDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Card getCardByUserId(int userId) {
        logger.info("Fetching card for user ID: {}", userId);
        
        // First check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new EntityNotFoundException("User not found with ID: " + userId);
                });
                
        logger.info("User found with ID: {}. Looking for associated card.", userId);
        
        return cardRepository.findByUser_Id(userId)
                .orElseThrow(() -> {
                    logger.error("Card not found for user ID: {}. User may not have a card associated.", userId);
                    return new EntityNotFoundException("Card not found for user ID: " + userId + ". User may not have a card associated.");
                });
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Card getCard(int id) {
        logger.info("Fetching card with ID: {}", id);
        return cardRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Card not found with ID: {}", id);
                    return new EntityNotFoundException("Card not found with ID: " + id);
                });
    }

    /**
     * Updates a card's balance by the specified amount
     * 
     * @param cardId The ID of the card to update
     * @param amount The amount to add (positive) or subtract (negative)
     * @return The updated card
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Card updateCardBalance(int cardId, float amount) {
        logger.info("Updating balance for card ID: {} by amount: {}", cardId, amount);
        
        try {
            // Get card in a separate transaction
            Card card = getCardWithCurrentBalance(cardId);
            float oldBalance = card.getBalance();
            float newBalance = oldBalance + amount;
            
            logger.info("Card balance before update - ID: {}, Current balance: {}, Change: {}, New balance: {}", 
                    cardId, oldBalance, amount, newBalance);
            
            // Ensure balance doesn't go negative
            if (newBalance < 0) {
                logger.error("Cannot update balance to negative value. Card ID: {}, Current: {}, Change: {}", 
                        cardId, oldBalance, amount);
                throw new IllegalArgumentException("Card balance cannot be negative");
            }
            
            card.setBalance(newBalance);
            
            // Save and immediately flush to DB
            Card updatedCard = cardRepository.saveAndFlush(card);
            logger.info("Updated card ID: {} from balance {} to {}", cardId, oldBalance, updatedCard.getBalance());
            
            // Verify the update went through
            verifyCardUpdate(cardId, newBalance);
            
            return updatedCard;
        } catch (Exception e) {
            logger.error("Error updating card balance: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update card balance: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get a card's current balance from the database
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    private Card getCardWithCurrentBalance(int cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with ID: " + cardId));
    }
    
    /**
     * Verify that a card update was successful
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    private void verifyCardUpdate(int cardId, float expectedBalance) {
        Card verifiedCard = cardRepository.findById(cardId).orElse(null);
        if (verifiedCard != null) {
            logger.info("Verified card ID: {} updated balance: {}", cardId, verifiedCard.getBalance());
            if (Math.abs(verifiedCard.getBalance() - expectedBalance) > 0.001) {
                logger.warn("Balance mismatch! Expected: {}, Actual: {}", expectedBalance, verifiedCard.getBalance());
            }
        } else {
            logger.warn("Could not verify card update - card not found after update");
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Card getCardByCardNumber(String cardNumber) {
        logger.info("Fetching card with card number: {}", cardNumber);
        return cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> {
                    logger.error("Card not found with card number: {}", cardNumber);
                    return new EntityNotFoundException("Card not found with card number: " + cardNumber);
                });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Card updateCardBalanceByCardNumber(String cardNumber, float amount) {
        logger.info("Updating balance for card number: {} by amount: {}", cardNumber, amount);
        
        try {
            Card card = getCardByCardNumber(cardNumber);
            float oldBalance = card.getBalance();
            float newBalance = oldBalance + amount;
            
            logger.info("Card balance before update - Number: {}, Current balance: {}, Change: {}, New balance: {}", 
                    cardNumber, oldBalance, amount, newBalance);
            
            if (newBalance < 0) {
                logger.error("Cannot update balance to negative value. Card number: {}, Current: {}, Change: {}", 
                        cardNumber, oldBalance, amount);
                throw new IllegalArgumentException("Card balance cannot be negative");
            }
            
            card.setBalance(newBalance);
            Card updatedCard = cardRepository.saveAndFlush(card);
            
            // Double-check saved balance
            Card verifiedCard = cardRepository.findByCardNumber(cardNumber).orElse(null);
            if (verifiedCard != null) {
                logger.info("Verified card number: {} updated balance: {}", cardNumber, verifiedCard.getBalance());
                if (Math.abs(verifiedCard.getBalance() - newBalance) > 0.001) {
                    logger.warn("Balance mismatch! Expected: {}, Actual: {}", newBalance, verifiedCard.getBalance());
                }
            }
            
            logger.info("Successfully updated card number: {} balance from {} to {}", 
                    cardNumber, oldBalance, updatedCard.getBalance());
            
            return updatedCard;
        } catch (Exception e) {
            logger.error("Error updating card balance by card number: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update card balance: " + e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Card updateCardUserId(int cardId, int newUserId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with ID: " + cardId));

        User newUser = userRepository.findById(newUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + newUserId));

        card.setUser(newUser);
        return cardRepository.save(card);
    }
}