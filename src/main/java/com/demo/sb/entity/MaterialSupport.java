package com.demo.sb.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "material_support")
public class MaterialSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    private String type; // e.g., "document", "image", "video"

    private String content; // URL or file path

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
}