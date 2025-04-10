package com.demo.sb.controllers;

import com.demo.sb.entity.CourseContent;
import com.demo.sb.service.CourseContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/course-content")
public class CourseContentController {

    @Autowired
    private CourseContentService contentService;

    @PostMapping(value = "/course/{courseId}/chapter/{chapterId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CourseContent> createContent(
            @PathVariable int courseId,
            @PathVariable int chapterId,
            @RequestPart(name = "content", required = false) CourseContent content, // Explicitly name the part
            @RequestPart(name = "file", required = false) MultipartFile file) throws IOException {
        // Validate content presence when no file is provided
        if (content == null && file == null) {
            throw new IllegalArgumentException("At least one of 'content' or 'file' must be provided");
        }

        // If content is null but file is provided, create a default CourseContent
        if (content == null) {
            content = new CourseContent();
        }

        CourseContent createdContent = contentService.createContent(content, courseId, chapterId, file);
        return new ResponseEntity<>(createdContent, HttpStatus.CREATED);
    }

    @GetMapping("/course/{courseId}/chapter/{chapterId}")
    public ResponseEntity<List<CourseContent>> getContentByCourseIdAndChapterId(
            @PathVariable int courseId,
            @PathVariable int chapterId) {
        List<CourseContent> contents = contentService.getContentByCourseIdAndChapterId(courseId, chapterId);
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/course/{courseId}/chapter/{chapterId}/download/{contentId}")
    public ResponseEntity<FileSystemResource> downloadFile(
            @PathVariable int courseId,
            @PathVariable int chapterId,
            @PathVariable int contentId) {
        File file = contentService.getFile(courseId, chapterId, contentId);
        FileSystemResource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        return new ResponseEntity<>("File processing error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}