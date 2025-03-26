package com.demo.sb.controllers;


import com.demo.sb.entity.QCM;
import com.demo.sb.service.QCMService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/qcms")
public class QCMController {
    @Autowired
    private QCMService qcmService;

    @PostMapping
    public ResponseEntity<QCM> createQCM(@Valid @RequestBody QCM qcm) {
        QCM savedQCM = qcmService.createQCM(qcm); // Assume this method exists
        return ResponseEntity.ok(savedQCM);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QCM> getQCMById(@PathVariable int id) {
        Optional<QCM> qcm = qcmService.findById(id); // Assume this method exists
        return qcm.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}