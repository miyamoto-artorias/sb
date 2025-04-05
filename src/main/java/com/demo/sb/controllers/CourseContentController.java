package com.demo.sb.controllers;


import com.demo.sb.entity.CourseContent;
import com.demo.sb.service.CourseContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-content")
public class CourseContentController {

    @Autowired
    private CourseContentService contentService;

    // Modified create content endpoint to include courseId
    @PostMapping("/course/{courseId}/chapter/{chapterId}")
    public ResponseEntity<CourseContent> createContent(
            @PathVariable int courseId,
            @PathVariable int chapterId,
            @RequestBody CourseContent content) {
        CourseContent createdContent = contentService.createContent(content, courseId, chapterId);
        return new ResponseEntity<>(createdContent, HttpStatus.CREATED);
    }

    // Modified to include courseId
    @GetMapping("/course/{courseId}/chapter/{chapterId}")
    public ResponseEntity<List<CourseContent>> getContentByCourseIdAndChapterId(
            @PathVariable int courseId,
            @PathVariable int chapterId) {
        List<CourseContent> contents = contentService.getContentByCourseIdAndChapterId(courseId, chapterId);
        return ResponseEntity.ok(contents);
    }

    // Modified to include courseId
    @GetMapping("/course/{courseId}/chapter/{chapterId}/type/{type}")
    public ResponseEntity<List<CourseContent>> getContentByTypeAndChapter(
            @PathVariable int courseId,
            @PathVariable int chapterId,
            @PathVariable String type) {
        List<CourseContent> contents = contentService.getContentByTypeCourseAndChapter(type, courseId, chapterId);
        return ResponseEntity.ok(contents);
    }



    // New endpoint for getting all content by courseId and chapterId
    @GetMapping("/course/{courseId}/chapter/{chapterId}/all")
    public ResponseEntity<List<CourseContent>> getAllContentByCourseIdAndChapterId(
            @PathVariable int courseId,
            @PathVariable int chapterId) {
        List<CourseContent> contents = contentService.getAllContentByCourseIdAndChapterId(courseId, chapterId);
        return ResponseEntity.ok(contents);
    }




    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}