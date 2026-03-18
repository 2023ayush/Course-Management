package com.ocms.coursemgmt.controller;
import com.ocms.coursemgmt.dto.MaterialRequest;
import com.ocms.coursemgmt.dto.MaterialResponse;
import com.ocms.coursemgmt.entity.Material;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.exception.ResourceNotFoundException;
import com.ocms.coursemgmt.repository.UserRepository;
import com.ocms.coursemgmt.service.MaterialService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    @Autowired
    MaterialService materialService;

    @Autowired
    UserRepository userRepository;


    @PostMapping("/upload")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<MaterialResponse> uploadMaterial(
@RequestParam String title,
@RequestParam String description,
@RequestParam Long courseId,
@RequestParam(required = false) MultipartFile file
    ) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User instructor = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor Not Found"));
        MaterialRequest request = new MaterialRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setCourseId(courseId);
        request.setFile(file);
return ResponseEntity.ok(materialService.uploadMaterial(request, instructor));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR')")
    public ResponseEntity<List<MaterialResponse>> getMaterials(@PathVariable Long courseId) {
        return ResponseEntity.ok(materialService.getMaterialsByCourse(courseId));
    }


    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR')")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Material material = materialService.getMaterialById(id);

        Resource resource = materialService.loadFileAsResource(material);
        if (resource == null) return ResponseEntity.notFound().build();

        String filename = new File(material.getFilePath()).getName();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .header("Content-Type", "application/octet-stream")
                .body(resource);
    }
}
