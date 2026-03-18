package com.ocms.coursemgmt.repository;

import com.ocms.coursemgmt.entity.Course;
import com.ocms.coursemgmt.entity.Enrollment;
import com.ocms.coursemgmt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(User student);
    boolean existsByStudentAndCourse(User student, Course course);

}
