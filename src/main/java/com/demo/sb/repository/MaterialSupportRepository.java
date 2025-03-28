package com.demo.sb.repository;


import com.demo.sb.entity.MaterialSupport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialSupportRepository extends JpaRepository<MaterialSupport, Integer> {
    List<MaterialSupport> findByCourseId(int courseId);

}