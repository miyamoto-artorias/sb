package com.demo.sb.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Entity
@Data
@Table(name = "users") // Rename table to "users"
@Inheritance(strategy = InheritanceType.JOINED) // For inheritance with Teacher and Student
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType; // TEACHER, STUDENT, or ADMIN


    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Should be hashed in production

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String fullName;

    @Column(nullable = true)
    private String profilePicture;  // Stores the file path or URL for the uploaded photo

    @Column(nullable = true)
    private String bio;  // Moved from Teacher to User for inheritance

    @Column(nullable = true)
    private String location;  // e.g., "city, country"

    @ElementCollection
    @CollectionTable(name = "user_preferred_languages", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> preferredLanguage;  // List of preferred languages

    @ElementCollection
    @CollectionTable(name = "user_social_links", joinColumns = @JoinColumn(name = "user_id"))
    private Map<String, String> socialLinks;  // Map for social links, e.g., {"twitter": "url"}

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true)
    @JsonIgnoreProperties({"user", "hibernateLazyInitializer", "handler"})
    private Card card; // The single card owned by this user


}