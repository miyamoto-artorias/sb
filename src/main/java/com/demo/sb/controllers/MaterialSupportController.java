package com.demo.sb.controllers;


import com.demo.sb.entity.MaterialSupport;
import com.demo.sb.service.MaterialSupportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/materials")
public class MaterialSupportController {
    @Autowired
    private MaterialSupportService materialSupportService;

    @PostMapping
    public ResponseEntity<MaterialSupport> createMaterial(@Valid @RequestBody MaterialSupport material) {
        MaterialSupport savedMaterial = materialSupportService.createMaterial(material); // Assume this method exists
        return ResponseEntity.ok(savedMaterial);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialSupport> getMaterialById(@PathVariable int id) {
        Optional<MaterialSupport> material = materialSupportService.findById(id); // Assume this method exists
        return material.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}