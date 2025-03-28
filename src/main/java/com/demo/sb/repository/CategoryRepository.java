package com.demo.sb.repository;


import com.demo.sb.entity.Category;
import com.demo.sb.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT c FROM Course c JOIN c.categories cat WHERE cat.id = :categoryId")
    List<Course> findCoursesByCategoryId(int categoryId);




}