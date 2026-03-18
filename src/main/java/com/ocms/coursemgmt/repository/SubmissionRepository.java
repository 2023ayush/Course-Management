package com.ocms.coursemgmt.repository;

import com.ocms.coursemgmt.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByAssignmentId(Long assignmentId);
    List<Submission> findByStudentId(Long studentId);
    Submission findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
}
