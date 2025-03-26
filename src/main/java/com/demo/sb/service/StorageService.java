package com.demo.sb.service;

import com.demo.sb.entity.FileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.demo.sb.repository.FileDataRepo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;





@Service
public class StorageService {
    @Autowired
    private  FileDataRepo fileDataRepo;
    private final String Folder_Path="C:\\Users\\altya\\Desktop\\proj_integ\\SBDB\\";

    public List<FileData> findAllElements(){
        return fileDataRepo.findAll();
    }



    public String uploadImage(MultipartFile file) throws IOException {
        String filePath=Folder_Path+file.getOriginalFilename();
        FileData fileData =fileDataRepo.save(FileData.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .filePath(filePath).build());
        file.transferTo(new File(filePath));
        if(fileData!=null){
            return "sucess !!"+filePath;
        }
        return "fail !!";
    }



    public byte[] downloadImage(String fileName) throws IOException {
        Optional<FileData> fileData = fileDataRepo.findByFileName(fileName);
        String filePath=fileData.get().getFilePath();
        //byte[] images = Files.readAllBytes(Paths.get(filePath));
        byte[] images = Files.readAllBytes(new File(filePath).toPath());
        return images;
    }











}
