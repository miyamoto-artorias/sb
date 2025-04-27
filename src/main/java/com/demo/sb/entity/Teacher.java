package com.demo.sb.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference // Add this to the "parent" side
    private List<Course> uploadedCourses;

    private float totalEarnings; // New field for total money made



}