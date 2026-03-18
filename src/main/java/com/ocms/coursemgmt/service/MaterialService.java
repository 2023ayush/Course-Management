package com.ocms.coursemgmt.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.ocms.coursemgmt.dto.MaterialRequest;
import com.ocms.coursemgmt.dto.MaterialResponse;
import com.ocms.coursemgmt.entity.Course;
import com.ocms.coursemgmt.entity.Material;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.exception.ResourceNotFoundException;
import com.ocms.coursemgmt.repository.CourseRepository;
import com.ocms.coursemgmt.repository.MaterialRepository;
import org.slf4j.ILoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private CourseRepository courseRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

public MaterialResponse uploadMaterial(MaterialRequest materialRequest, User instructor){
    Course course  =  courseRepository.findById(materialRequest.getCourseId()).orElseThrow(() -> new ResourceNotFoundException("Course Not Found"));

    String filePath = null;
    MultipartFile file = materialRequest.getFile();
    if(file != null && !file.isEmpty()){

        long maxSize = 5 * 1024 * 1024;
        if(file.getSize() > maxSize){
            throw new ResourceNotFoundException("File Size must be less than 5MB");
        }


        if (!file.getContentType().equals("application/pdf") ||
                !file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            throw new ResourceNotFoundException("Only PDF files are allowed");
        }

        File uploadFolder = new File(uploadDir);
        if(!uploadFolder.exists()) uploadFolder.mkdirs();
        filePath = uploadDir + file.getOriginalFilename();
        try {
            file.transferTo(new File(filePath));
            System.out.println("File saved to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("File upload failed");
        }

    }

    Material material = new Material();
    material.setTitle(materialRequest.getTitle());
    material.setDescription(materialRequest.getDescription());
    material.setFilePath(filePath);
    material.setCourse(course);
    material.setInstructor(instructor);


    materialRepository.save(material);

    MaterialResponse response = new MaterialResponse();
    response.setId(material.getId());
    response.setTitle(material.getTitle());
    response.setDescription(material.getDescription());
    String downloadUrl = "/download/" + material.getId();
    response.setFilePath(downloadUrl);

    response.setCourseName(course.getTitle());
    return response;
}


public List<MaterialResponse> getMaterialsByCourse(Long courseId){
    List<Material> materials = materialRepository.findByCourseId(courseId);
    List<MaterialResponse> responses = new ArrayList<>();
    for(Material material : materials){
        MaterialResponse response = new MaterialResponse();
        response.setId(material.getId());
        response.setTitle(material.getTitle());
        response.setDescription(material.getDescription());
        String downloadUrl = "/download/" + material.getId();
        response.setFilePath(downloadUrl);
        response.setCourseName(material.getCourse().getTitle());
        responses.add(response);
    }
    return responses;
}

public Material getMaterialById(Long id){
    return materialRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Materials not found with id " + id));


}

public Resource loadFileAsResource(Material material){
    if(material.getFilePath() == null) return null;
    File file = new File(material.getFilePath());
    if(!file.exists()) return null;
    return new FileSystemResource(file);
}

}
