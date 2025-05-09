package com.demo.sb.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.List;

@Data
public class CourseRequestDTO {
    private Integer id;
    
    @NotNull(message = "Student ID is required")
    private Integer studentId;
    
    @NotNull(message = "Teacher ID is required")
    private Integer teacherId;
    
    @NotEmpty(message = "Subject is required")
    private String subject;
    
    @NotNull(message = "At least one category is required")
    private List<Integer> categoryIds;
    
    @PositiveOrZero(message = "Price must be zero or positive")
    private float price;
    
    private String status = "pending"; // Default value
}