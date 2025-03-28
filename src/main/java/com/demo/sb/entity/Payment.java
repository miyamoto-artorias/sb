package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private float amount;

    private LocalDateTime date;

    private String status; // e.g., "pending", "completed"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id")
    private User payer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Teacher receiver;
    //To query all payments a teacher received with your current setup:
    //Use a JPQL query: "SELECT p FROM Payment p WHERE p.receiver = :teacher".

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card; // The card used for this payment

}