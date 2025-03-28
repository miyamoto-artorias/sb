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

    @PostMapping("/{userId}")
    public ResponseEntity<Card> createCard(@RequestBody Card card, @PathVariable int userId) {
        return ResponseEntity.ok(cardService.createCard(card, userId));
    }




    @GetMapping("/{id}")
    public ResponseEntity<Card> getCard(@PathVariable int id) {
        return ResponseEntity.ok(cardService.getCard(id));
    }

}