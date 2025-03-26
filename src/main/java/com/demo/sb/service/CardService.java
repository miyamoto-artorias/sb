package com.demo.sb.service;


import com.demo.sb.entity.Card;
import com.demo.sb.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CardService {
    @Autowired
    private CardRepository cardRepository;

    @Transactional
    public Card createCard(Card card) {
        return cardRepository.save(card);
    }

    public Optional<Card> findById(int id) {
        return cardRepository.findById(id);
    }
}