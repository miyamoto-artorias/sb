package com.demo.sb.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

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

    // Common course enrollment for both Teachers and Students
    
    @ManyToMany
    @JoinTable(
            name = "user_course_enrollment",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> enrolledCourses;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true)
    @JsonManagedReference // Add this to the "parent" side
    private Card card; // The single card owned by this user


}