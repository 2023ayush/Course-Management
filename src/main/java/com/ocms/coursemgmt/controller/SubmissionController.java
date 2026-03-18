package com.ocms.coursemgmt.controller;

import com.ocms.coursemgmt.dto.GradeRequest;
import com.ocms.coursemgmt.dto.SubmissionResponse;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.exception.ResourceNotFoundException;
import com.ocms.coursemgmt.repository.UserRepository;
import com.ocms.coursemgmt.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/submit")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final UserRepository userRepository;

    public SubmissionController(SubmissionService submissionService, UserRepository userRepository){
        this.submissionService = submissionService;
        this.userRepository = userRepository;
    }

    @PostMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionResponse> submitAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file")
    MultipartFile file) throws Exception{

        User student = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

if(file == null || file.isEmpty()){
    throw new ResourceNotFoundException("No Files Selected for Submission");
}

String uploadDir = System.getProperty("user.dir") + "/uploads/assignments/";
File UploadFolder = new File(uploadDir);
if(!UploadFolder.exists()) UploadFolder.mkdirs();


String filePath = uploadDir + file.getOriginalFilename();
file.transferTo(new java.io.File(filePath));

SubmissionResponse submission = submissionService.submitAssignment(assignmentId,student,filePath);
return ResponseEntity.ok(submission);
    }


    @PutMapping("/{id}/grade")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<SubmissionResponse> gradeSubmission(
        @PathVariable Long id,
        @RequestBody GradeRequest request
        ){
        return ResponseEntity.ok(submissionService.gradeSubmission(id, request));
}
    @GetMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionByAssignment(@PathVariable Long assignmentId){
        return ResponseEntity.ok(submissionService.getSubmissionsByAssignment(assignmentId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<SubmissionResponse>> getStudentSubmission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(submissionService.getSubmissionsByStudent(student.getId()));
    }

}
