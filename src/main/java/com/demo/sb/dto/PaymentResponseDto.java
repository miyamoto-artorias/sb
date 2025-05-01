package com.demo.sb.dto;

import com.demo.sb.entity.Payment;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDto {
    private int id;
    private float amount;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    private String status;
    private int courseId;
    private int payerId;
    private int receiverId;
    private int cardId;

    public static PaymentResponseDto fromEntity(Payment payment) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setDate(payment.getDate());
        dto.setStatus(payment.getStatus());
        dto.setCourseId(payment.getCourse().getId());
        dto.setPayerId(payment.getPayer().getId());
        dto.setReceiverId(payment.getReceiver().getId());
        dto.setCardId(payment.getCard().getId());
        return dto;
    }
} 