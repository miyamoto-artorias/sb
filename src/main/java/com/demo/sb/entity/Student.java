package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Student extends User {
    private float progress;

    @ManyToMany
    @JoinTable(
            name = "student_course_enrollment",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> enrolledCourses;

    private int points;
}