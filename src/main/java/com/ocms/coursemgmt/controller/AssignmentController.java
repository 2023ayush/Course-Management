package com.ocms.coursemgmt.controller;

import com.ocms.coursemgmt.dto.AssignmentRequest;
import com.ocms.coursemgmt.dto.AssignmentResponse;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.repository.UserRepository;
import com.ocms.coursemgmt.service.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final UserRepository userRepository;

    public AssignmentController(AssignmentService assignmentService, UserRepository userRepository){
        this.assignmentService = assignmentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/course/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<AssignmentResponse> createAssignment(
            @PathVariable Long courseId,
            @RequestBody AssignmentRequest request,
            Authentication authentication) {


        String email = authentication.getName();
        User instructor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));


        AssignmentResponse assignmentResponse = assignmentService.createAssignment(courseId, request, instructor);

        return ResponseEntity.ok(assignmentResponse);
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR')")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentByCourse(@PathVariable Long courseId){
        List<AssignmentResponse> assignments = assignmentService.getAssignmentByCourse(courseId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR')")
    public ResponseEntity<AssignmentResponse> getAssignmentyId(@PathVariable Long id){
        AssignmentResponse assignment = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<String> deleteAssignment(@PathVariable Long id){
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok("Assignment Deleted Successfully");
    }
}
