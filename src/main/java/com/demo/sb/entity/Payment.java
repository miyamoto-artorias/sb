package com.demo.sb.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User payer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User receiver; // Changed from Teacher to User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Card card; // The card used for this payment
}