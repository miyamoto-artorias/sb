package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true) //to ensure proper inheritance handling in equals and hashCode methods -> zadha claude
public class Student extends User {

    // No need for anything they r all in user class
/*
    private float progress;
    private int points;

    @ManyToMany
    @JoinTable(
            name = "student_course_enrollment",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> enrolledCourses;
    */
}