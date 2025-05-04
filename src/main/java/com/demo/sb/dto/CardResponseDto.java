package com.demo.sb.dto;

import com.demo.sb.entity.Card;
import lombok.Data;

@Data
public class CardResponseDto {
    private int id;
    private boolean valid;
    private float balance;
    private int userId;
    private String cardNumber; // New field for card number

    public static CardResponseDto fromEntity(Card card) {
        CardResponseDto dto = new CardResponseDto();
        dto.setId(card.getId());
        dto.setValid(card.isValid());
        dto.setBalance(card.getBalance());
        dto.setCardNumber(card.getCardNumber()); // Include cardNumber
        if (card.getUser() != null) {
            dto.setUserId(card.getUser().getId());
        }
        return dto;
    }
}