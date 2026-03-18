package com.ocms.coursemgmt.controller;

import com.ocms.coursemgmt.entity.Course;
import com.ocms.coursemgmt.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService){
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/{courseId}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> enrolledCourse(@PathVariable Long courseId){
        enrollmentService.enrollCourse(courseId);
        return ResponseEntity.ok("Successfully Enrolled in a Course");
    }

    @GetMapping("/enrolled")
    @PreAuthorize("hasRole('STUDENT')")
    public List<Course> getEnrolledCourse(){
        return enrollmentService.getEnrolledCourse();

    }

}
