package com.ocms.coursemgmt.service;

import com.ocms.coursemgmt.dto.GradeRequest;
import com.ocms.coursemgmt.dto.SubmissionResponse;
import com.ocms.coursemgmt.entity.Assignment;
import com.ocms.coursemgmt.entity.Submission;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.exception.ResourceNotFoundException;
import com.ocms.coursemgmt.repository.AssignmentRepository;
import com.ocms.coursemgmt.repository.SubmissionRepository;
import com.ocms.coursemgmt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public SubmissionService(SubmissionRepository submissionRepository,
                             AssignmentRepository assignmentRepository,
                             UserRepository userRepository) {

        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
    }

    // Student submits assignment
    public SubmissionResponse submitAssignment(Long assignmentId, User student, String filePath) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        // Check deadline
        if (LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new ResourceNotFoundException("Submission deadline passed");
        }

        // Get full student entity
        User fullStudent = userRepository.findById(student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(fullStudent);
        submission.setFilePath(filePath);
        submission.setSubmittedAt(LocalDateTime.now());

        Submission saved = submissionRepository.save(submission);

        return mapToResponse(saved);
    }


    // Instructor grades submission
    public SubmissionResponse gradeSubmission(Long id, GradeRequest request) {

        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        submission.setGrade(request.getGrade());
        submission.setFeedback(request.getFeedback());

        Submission saved = submissionRepository.save(submission);

        return mapToResponse(saved);
    }


    // Instructor view submissions of an assignment
    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {

        return submissionRepository.findByAssignmentId(assignmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    // Student view their submissions
    public List<SubmissionResponse> getSubmissionsByStudent(Long studentId) {

        return submissionRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    // Convert Submission entity → Response DTO
    private SubmissionResponse mapToResponse(Submission submission) {

        SubmissionResponse response = new SubmissionResponse();

        response.setId(submission.getId());

        if (submission.getAssignment() != null) {
            response.setAssignmentId(submission.getAssignment().getId());
        }

        if (submission.getStudent() != null) {
            response.setStudentId(submission.getStudent().getId());
        }

        response.setFilePath(submission.getFilePath());
        response.setSubmittedAt(submission.getSubmittedAt());
        response.setGrade(submission.getGrade());
        response.setFeedback(submission.getFeedback());

        return response;
    }
}