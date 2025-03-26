package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Teacher extends User {
    private String bio;

    @ElementCollection
    @CollectionTable(name = "teacher_expertise", joinColumns = @JoinColumn(name = "teacher_id"))
    private List<String> expertise;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Course> uploadedCourses;

    @ManyToMany
    @JoinTable(
            name = "teacher_course_enrollment",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> enrolledCourses; // Teachers can enroll like Students
}