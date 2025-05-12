package com.demo.sb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    private float progress; // Progress percentage (0-100)
    private int points;    // Points specific to this course (optional)
    
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserContentProgress> contentProgresses = new ArrayList<>();

    // Helper method to update progress based on completed content
    public void updateProgress() {
        if (course == null || course.getChapters() == null) {
            this.progress = 0;
            return;
        }
        
        // Count total course content
        int totalContentCount = 0;
        for (CourseChapter chapter : course.getChapters()) {
            if (chapter.getContents() != null) {
                totalContentCount += chapter.getContents().size();
            }
        }
        
        // If no content, progress is 0
        if (totalContentCount == 0) {
            this.progress = 0;
            return;
        }
        
        // Count completed content
        int completedContentCount = 0;
        for (UserContentProgress progress : contentProgresses) {
            if (progress.isCompleted()) {
                completedContentCount++;
            }
        }
        
        // Calculate percentage (0-100)
        this.progress = (float) completedContentCount / totalContentCount * 100;
    }

    // Add a helper method to get completed content IDs
    public List<Integer> getCompletedContentIds() {
        List<Integer> completedContentIds = new ArrayList<>();
        for (UserContentProgress progress : contentProgresses) {
            if (progress.isCompleted() && progress.getContent() != null) {
                completedContentIds.add(progress.getContent().getId());
            }
        }
        return completedContentIds;
    }

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


