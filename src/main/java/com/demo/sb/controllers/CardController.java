package com.demo.sb.controllers;


import com.demo.sb.entity.Card;
import com.demo.sb.service.CardService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import com.demo.sb.dto.CardRequestDto;
import com.demo.sb.dto.CardResponseDto;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping
    public ResponseEntity<?> createCard(@Valid @RequestBody CardRequestDto cardDto) {
        try {
            Card savedCard = cardService.createCard(cardDto);
            return ResponseEntity.ok(CardResponseDto.fromEntity(savedCard));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage()); // 409 Conflict
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the card.");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CardResponseDto> getCardByUserId(@PathVariable int userId) {
        try {
            Card card = cardService.getCardByUserId(userId);
            return ResponseEntity.ok(CardResponseDto.fromEntity(card));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> getCard(@PathVariable int id) {
        try {
            Card card = cardService.getCard(id);
            return ResponseEntity.ok(CardResponseDto.fromEntity(card));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/card-number/{cardNumber}")
    public ResponseEntity<CardResponseDto> getCardByCardNumber(@PathVariable String cardNumber) {
        try {
            Card card = cardService.getCardByCardNumber(cardNumber);
            return ResponseEntity.ok(CardResponseDto.fromEntity(card));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/card-number/{cardNumber}/balance")
    public ResponseEntity<CardResponseDto> updateCardBalanceByCardNumber(@PathVariable String cardNumber, @RequestParam float amount) {
        try {
            Card updatedCard = cardService.updateCardBalanceByCardNumber(cardNumber, amount);
            return ResponseEntity.ok(CardResponseDto.fromEntity(updatedCard));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{cardId}/user/{newUserId}")
    public ResponseEntity<CardResponseDto> updateCardUserId(@PathVariable int cardId, @PathVariable int newUserId) {
        try {
            Card updatedCard = cardService.updateCardUserId(cardId, newUserId);
            return ResponseEntity.ok(CardResponseDto.fromEntity(updatedCard));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}