package com.demo.sb.dto;

import lombok.Data;

@Data
public class CardRequestDto {
    private String password;
    private boolean valid;
    private float balance;
    private int userId; // User ID to associate the card with
} 