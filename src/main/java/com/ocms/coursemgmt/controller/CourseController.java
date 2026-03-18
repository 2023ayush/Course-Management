package com.ocms.coursemgmt.controller;

import com.ocms.coursemgmt.dto.CourseRequest;
import com.ocms.coursemgmt.entity.Course;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.repository.UserRepository;
import com.ocms.coursemgmt.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final UserRepository userRepository;

    public CourseController(CourseService courseService, UserRepository userRepository){
        this.courseService = courseService;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Course> createCourse(@RequestBody CourseRequest request,
                                               Authentication authentication) {
        String email = authentication.getName();
        User instructor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Set<Long> instructorIds = new HashSet<>();
        instructorIds.add(instructor.getId());


        if (request.getInstructorIds() != null) {
            instructorIds.addAll(request.getInstructorIds());
        }

        Course course = courseService.createCourse(
                request.getTitle(),
                request.getDescription(),
                request.getSchedule(),
                instructorIds
        );

        return ResponseEntity.ok(course);
    }
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public Course updateCourse(@PathVariable Long id,
                               @RequestBody CourseRequest courseRequest,
                               Authentication authentication) {


        String email = authentication.getName();
        User loggedInInstructor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        return courseService.updateCourse(id, courseRequest, loggedInInstructor);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id){
         courseService.deleteCourse(id);
         return ResponseEntity.ok("Course deleted Successfully");
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN','INSTRUCTOR')")
    public List<Course> getAllCourse(){
        return courseService.getAllCourse();
    }

    @PostMapping("/{courseId}/add-instructor/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Course addInstructor(@PathVariable Long courseId, @PathVariable Long instructorId){
        return courseService.addInstructor(courseId,instructorId);
    }

    @DeleteMapping("/{courseId}/remove-instructor/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> removeInstructor(
            @PathVariable Long courseId,
            @PathVariable Long instructorId) {

        String message = courseService.removeInstructor(courseId, instructorId);

        return ResponseEntity.ok(message);
    }
    }


