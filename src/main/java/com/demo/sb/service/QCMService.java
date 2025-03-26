package com.demo.sb.service;


import com.demo.sb.entity.QCM;
import com.demo.sb.repository.QCMRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class QCMService {
    @Autowired
    private QCMRepository qcmRepository;

    @Transactional
    public QCM createQCM(QCM qcm) {
        return qcmRepository.save(qcm);
    }

    public Optional<QCM> findById(int id) {
        return qcmRepository.findById(id);
    }
}