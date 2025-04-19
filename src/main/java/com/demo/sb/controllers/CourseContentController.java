package com.demo.sb.controllers;

import com.demo.sb.entity.CourseContent;
import com.demo.sb.service.CourseContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
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

    /**
     * JSON-only endpoint: accepts application/json and application/json;charset=UTF-8
     *
     * POST /api/course-content/course/{courseId}/chapter/{chapterId}
     * Headers: Content-Type: application/json;charset=UTF-8 (or application/json)
     * Body: {"title":"...","type":"video","content":"..."}
     */
    @PostMapping(
            value = "/course/{courseId}/chapter/{chapterId}",
            consumes = { MediaType.APPLICATION_JSON_VALUE, "application/json;charset=UTF-8" }
    )
    public ResponseEntity<CourseContent> createJsonContent(
            @PathVariable int courseId,
            @PathVariable int chapterId,
            @RequestBody CourseContent content
    ) throws IOException {
        CourseContent created = contentService.createContent(content, courseId, chapterId, null);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * PDF (or any file) upload endpoint
     *
     * POST /api/course-content/course/{courseId}/chapter/{chapterId}/upload
     * Content-Type: multipart/form-data
     * Form-data keys: title (Text), type (Text), file (File)
     */
    @PostMapping(
            value = "/course/{courseId}/chapter/{chapterId}/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CourseContent> uploadFileContent(
            @PathVariable int courseId,
            @PathVariable int chapterId,
            @RequestParam("title") String title,
            @RequestParam("type")  String type,
            @RequestPart("file")    MultipartFile file
    ) throws IOException {
        CourseContent content = new CourseContent();
        content.setTitle(title);
        content.setType(type);

        CourseContent created = contentService.createContent(content, courseId, chapterId, file);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/course/{courseId}/chapter/{chapterId}")
    public ResponseEntity<List<CourseContent>> getContentByCourseIdAndChapterId(
            @PathVariable int courseId,
            @PathVariable int chapterId
    ) {
        List<CourseContent> contents = contentService.getContentByCourseIdAndChapterId(courseId, chapterId);
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/course/{courseId}/chapter/{chapterId}/download/{contentId}")
    public ResponseEntity<FileSystemResource> downloadFile(
            @PathVariable int courseId,
            @PathVariable int chapterId,
            @PathVariable int contentId
    ) {
        File file = contentService.getFile(courseId, chapterId, contentId);
        FileSystemResource resource = new FileSystemResource(file);

        String filename = file.getName().toLowerCase();
        MediaType mediaType = filename.endsWith(".pdf")
                ? MediaType.APPLICATION_PDF
                : MediaType.APPLICATION_OCTET_STREAM;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.inline().filename(file.getName()).build());
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setPragma("no-cache");
        headers.setExpires(0);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(mediaType)
                .body(resource);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        return new ResponseEntity<>("File processing error: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
