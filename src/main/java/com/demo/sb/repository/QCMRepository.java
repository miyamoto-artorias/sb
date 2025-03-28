package com.demo.sb.repository;


import com.demo.sb.entity.QCM;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QCMRepository extends JpaRepository<QCM, Integer> {
    List<QCM> findByCourseId(int courseId);

}