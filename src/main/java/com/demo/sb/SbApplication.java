package com.demo.sb;

import com.demo.sb.entity.FileData;
import com.demo.sb.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/files")
public class SbApplication {
	@Autowired
	private  StorageService storageService;
	@Autowired
	public SbApplication(StorageService storageService) {
		this.storageService = storageService;
	}


	@GetMapping("/all")
	public List<FileData> getAllFiles() {
		return storageService.findAllElements();
	}

	@PostMapping("/upload" )
	public ResponseEntity<? > uploadImageToFIleSystem(@RequestParam("image") MultipartFile file) throws IOException {
		String uploadImage = storageService.uploadImage(file);
		return ResponseEntity.status(HttpStatus.OK)
				.body(uploadImage);
	}

	@GetMapping("/{fileName}")
	public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
		byte[] imageData=storageService.downloadImage(fileName);
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.valueOf("image/png"))
				.body(imageData);

	}


	public static void main(String[] args) {
		SpringApplication.run(SbApplication.class, args);
	}


}
