package com.demo.sb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CourseContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String content;
    private String type;

    // Added the missing inverse relationship
    @ManyToOne
    @JoinColumn(name = "chapter_id")
    @JsonBackReference
    private CourseChapter chapter;

}
