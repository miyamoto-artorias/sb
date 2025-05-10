package com.demo.sb.entity;

import lombok.Data;
import lombok.ToString;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@ToString
public class PaymentRequest {
    @Positive(message = "Amount must be positive")
    private float amount;
    
    @NotNull(message = "Payer ID is required")
    private Integer payerId;
    
    @NotNull(message = "Receiver ID is required")
    private Integer receiverId;
    
    @NotNull(message = "Course ID is required")
    private Integer courseId;
    
    @NotNull(message = "Card ID is required")
    private Integer cardId;
}