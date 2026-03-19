package com.ocms.coursemgmt.service;
import com.ocms.coursemgmt.dto.AssignmentRequest;
import com.ocms.coursemgmt.dto.AssignmentResponse;
import com.ocms.coursemgmt.entity.Assignment;
import com.ocms.coursemgmt.entity.Course;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.exception.ResourceNotFoundException;
import com.ocms.coursemgmt.repository.AssignmentRepository;
import com.ocms.coursemgmt.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository){
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
    }

    public AssignmentResponse createAssignment(Long courseId, AssignmentRequest request, User instructor) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course Not Found"));


        if (!course.getInstructors().contains(instructor)) {
            throw new RuntimeException("Instructor is not assigned to this course");
        }


        Assignment assignment = new Assignment();
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate().atTime(23, 59, 59));
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setCourse(course);
        assignment.setInstructor(instructor);

        Assignment saved = assignmentRepository.save(assignment);

        return mapToResponse(saved);
    }

    public List<AssignmentResponse> getAssignmentByCourse(Long courseId){
        List<Assignment> assignments = assignmentRepository.findByCourseId(courseId);
        return assignments.stream().map(this::mapToResponse).toList();
    }

    public AssignmentResponse getAssignmentById(Long id){
        Assignment assignment =  assignmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
          return mapToResponse(assignment);
    }

    public void deleteAssignment(Long id){
        Assignment assignment = assignmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Assignment Not Found"));
        assignmentRepository.delete(assignment);
    }



    private AssignmentResponse mapToResponse(Assignment assignment){
        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setTitle(assignment.getTitle());
        response.setDescription(assignment.getDescription());
        response.setDueDate(assignment.getDueDate());
        response.setCourseId(assignment.getCourse().getId());
        response.setInstructorId(assignment.getInstructor() != null ? assignment.getInstructor().getId() : null);
        return response;
    }

}
