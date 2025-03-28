package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true) //to ensure proper inheritance handling in equals and hashCode methods -> zadha claude
public class Teacher extends User {
    private String bio;

    @ElementCollection
    @CollectionTable(name = "teacher_expertise", joinColumns = @JoinColumn(name = "teacher_id"))
    private List<String> expertise;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Course> uploadedCourses;

    private float totalEarnings; // New field for total money made


    /*
    @ManyToMany
    @JoinTable(
            name = "teacher_course_enrollment",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> enrolledCourses; // Teachers can enroll like Students
    */
}