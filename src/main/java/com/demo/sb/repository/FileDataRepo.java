package com.demo.sb.repository;

import com.demo.sb.entity.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileDataRepo extends JpaRepository<FileData, Long> {
     Optional<FileData> findByFileName(String fileName);

}
