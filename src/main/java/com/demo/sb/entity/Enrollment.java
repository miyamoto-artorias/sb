package com.demo.sb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "enrollment",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "course_id"}))
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    private float progress; // Progress specific to this course for this user
    private int points;    // Points specific to this course (optional)

    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }

    public Integer getCourseId() {
        return course != null ? course.getId() : null;
    }

    // Add these fields for request/response handling
    @Transient
    @JsonProperty("userId")
    private Integer userId;

    @Transient
    @JsonProperty("courseId")
    private Integer courseId;
}


