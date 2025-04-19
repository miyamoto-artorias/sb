package com.demo.sb.controllers;


import com.demo.sb.entity.CourseChapter;
import com.demo.sb.service.CourseChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-chapters")
public class CourseChapterController {

    @Autowired
    private CourseChapterService chapterService;

    // Create new chapter
// In CourseChapterController.java
    @PostMapping(
            value = "/course/{courseId}",
            consumes = {"application/json", "application/json;charset=UTF-8"}
    )
    public ResponseEntity<CourseChapter> createChapter(
            @PathVariable int courseId,
            @ModelAttribute CourseChapter chapter) { // Changed from @RequestBody
        CourseChapter createdChapter = chapterService.createChapter(chapter, courseId);
        return new ResponseEntity<>(createdChapter, HttpStatus.CREATED);
    }

    // Get chapter by ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseChapter> getChapterById(@PathVariable int id) {
        CourseChapter chapter = chapterService.getChapterById(id);
        return ResponseEntity.ok(chapter);
    }

    // Get all chapters for a course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseChapter>> getChaptersByCourseId(@PathVariable int courseId) {
        List<CourseChapter> chapters = chapterService.getChaptersByCourseId(courseId);
        return ResponseEntity.ok(chapters);
    }

    // Update chapter
    @PutMapping("/{id}")
    public ResponseEntity<CourseChapter> updateChapter(
            @PathVariable int id,
            @RequestBody CourseChapter chapter) {
        CourseChapter updatedChapter = chapterService.updateChapter(id, chapter);
        return ResponseEntity.ok(updatedChapter);
    }

    // Delete chapter
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable int id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }

    // Exception handler for invalid arguments
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}