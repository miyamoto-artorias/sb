package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED) // For inheritance with Teacher and Student
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Should be hashed in production

    @Column(nullable = false, unique = true)
    private String email;
}