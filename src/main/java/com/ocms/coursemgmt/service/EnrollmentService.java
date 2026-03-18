package com.ocms.coursemgmt.service;

import com.ocms.coursemgmt.entity.Course;
import com.ocms.coursemgmt.entity.Enrollment;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.exception.ResourceNotFoundException;
import com.ocms.coursemgmt.repository.CourseRepository;
import com.ocms.coursemgmt.repository.EnrollmentRepository;
import com.ocms.coursemgmt.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(CourseRepository courseRepository, UserRepository userRepository, EnrollmentRepository enrollmentRepository){
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public void enrollCourse(Long courseId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User student = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Student Not Found"));

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course Not Found"));

        if(enrollmentRepository.existsByStudentAndCourse(student,course)){
            throw new ResourceNotFoundException("Already enrolled in this course");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollmentRepository.save(enrollment);
    }

        public List<Course> getEnrolledCourse(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User student = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Student Not Found"));
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
        return enrollments.stream().map(Enrollment::getCourse).collect(Collectors.toList());
}


}
