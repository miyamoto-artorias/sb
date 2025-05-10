package com.demo.sb.dto;

import com.demo.sb.entity.Payment;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private int id;
    private float amount;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    
    private String status;
    private int courseId;
    private int payerId;
    private int receiverId;
    private int cardId;
    
    // Default constructor for Jackson deserialization
    public PaymentDTO() {
    }
    
    /**
     * Creates a PaymentDTO from a Payment entity with safe null checking
     */
    public static PaymentDTO fromEntity(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setDate(payment.getDate());
        dto.setStatus(payment.getStatus());
        
        // Add null checks for entity relationships
        if (payment.getCourse() != null) {
            dto.setCourseId(payment.getCourse().getId());
        }
        
        if (payment.getPayer() != null) {
            dto.setPayerId(payment.getPayer().getId());
        }
        
        if (payment.getReceiver() != null) {
            dto.setReceiverId(payment.getReceiver().getId());
        }
        
        if (payment.getCard() != null) {
            dto.setCardId(payment.getCard().getId());
        }
        
        return dto;
    }
} 