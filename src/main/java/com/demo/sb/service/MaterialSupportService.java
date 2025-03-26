package com.demo.sb.service;


import com.demo.sb.entity.MaterialSupport;
import com.demo.sb.repository.MaterialSupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MaterialSupportService {
    @Autowired
    private MaterialSupportRepository materialSupportRepository;

    @Transactional
    public MaterialSupport createMaterial(MaterialSupport material) {
        return materialSupportRepository.save(material);
    }

    public Optional<MaterialSupport> findById(int id) {
        return materialSupportRepository.findById(id);
    }
}