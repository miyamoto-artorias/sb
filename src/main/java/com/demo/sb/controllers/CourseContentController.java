package com.demo.sb.controllers;

import com.demo.sb.dto.CourseContentDTO;
import com.demo.sb.entity.CourseContent;
import com.demo.sb.service.CourseContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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

    // ─── 1) JSON‑only endpoint ───────────────────────────────────────────────────
    // POST /api/course-content/course/{courseId}/chapter/{chapterId}
    // Content-Type: application/json
    // Body:
    // {
    //   "title":   "lolo",
    //   "type":    "video",
    //   "content": "https://www.youtube.com/…"
    // }
    @PostMapping(
            value = "/course/{courseId}/chapter/{chapterId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CourseContent> createJsonContent(
            @PathVariable int courseId,
            @PathVariable int chapterId,
            @RequestBody CourseContentDTO courseContentDTO
    ) throws IOException {
        CourseContent content = new CourseContent();
        content.setTitle(courseContentDTO.getTitle());
        content.setContent(courseContentDTO.getContent());
        content.setType(courseContentDTO.getType());

        CourseContent created = contentService.createContent(content, courseId, chapterId, null);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ─── 2) PDF (or any file) upload endpoint ───────────────────────────────────
    // POST /api/course-content/course/{courseId}/chapter/{chapterId}/upload
    // Content-Type: multipart/form-data
    // Form‑data keys:
    //   title (Text)  → e.g. "Chapter 1 PDF"
    //   type  (Text)  → must be "pdf" (or "video")
    //   file  (File)  → your .pdf or video file
    @PostMapping(
            value    = "/course/{courseId}/chapter/{chapterId}/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CourseContent> uploadFileContent(
            @PathVariable int courseId,
            @PathVariable int chapterId,
            @RequestParam("title") String title,
            @RequestParam("type")  String type,
            @RequestPart("file")    MultipartFile file
    ) throws IOException {
        // build the DTO from the form‑fields
        CourseContent content = new CourseContent();
        content.setTitle(title);
        content.setType(type);

        CourseContent created = contentService.createContent(content, courseId, chapterId, file);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping(
            value = "/course/{courseId}/chapter/{chapterId}/upload-video",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CourseContent> uploadVideoContent(
            @PathVariable int courseId,
            @PathVariable int chapterId,
            @RequestParam("title") String title,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        // Build the CourseContent entity
        CourseContent content = new CourseContent();
        content.setTitle(title);
        content.setType("video");

        // Use the service to handle the upload
        CourseContent created = contentService.createContent(content, courseId, chapterId, file);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
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

        String fileName = file.getName().toLowerCase();
        MediaType mediaType = fileName.endsWith(".pdf") ? MediaType.APPLICATION_PDF : MediaType.APPLICATION_OCTET_STREAM;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + file.getName()); // <--- inline for browser display
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(mediaType)
                .body(resource);
    }

    @GetMapping("/course/{courseId}/chapter/{chapterId}/stream-video/{contentId}")
    public ResponseEntity<Resource> streamVideo(
            @PathVariable int courseId,
            @PathVariable int chapterId,
            @PathVariable int contentId,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader
    ) throws IOException {
        CourseContent content = contentService.getContentById(contentId);

        File videoFile = new File(content.getContent());
        if (!videoFile.exists() || !videoFile.isFile()) {
            throw new IllegalArgumentException("Video file not found");
        }

        return contentService.prepareVideoStream(videoFile, rangeHeader);
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