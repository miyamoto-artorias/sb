package com.demo.sb.controllers;


import com.demo.sb.entity.Card;
import com.demo.sb.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping
    public ResponseEntity<Card> createCard(@Valid @RequestBody Card card) {
        Card savedCard = cardService.createCard(card); // Assume this method exists
        return ResponseEntity.ok(savedCard);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable int id) {
        Optional<Card> card = cardService.findById(id); // Assume this method exists
        return card.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}