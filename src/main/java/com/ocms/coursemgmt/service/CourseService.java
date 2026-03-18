package com.ocms.coursemgmt.service;

import com.ocms.coursemgmt.dto.CourseRequest;
import com.ocms.coursemgmt.entity.Course;
import com.ocms.coursemgmt.entity.Role;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.exception.ResourceNotFoundException;
import com.ocms.coursemgmt.repository.CourseRepository;
import com.ocms.coursemgmt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CourseService {

private final CourseRepository courseRepository;
private final UserRepository userRepository;

public CourseService(CourseRepository courseRepository, UserRepository userRepository){
    this.courseRepository = courseRepository;
    this.userRepository = userRepository;

}

  public Course createCourse(String title, String description, String schedule, Set<Long> instructorIds) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setSchedule(schedule);

        Set<User> instructors = new HashSet<>();
        for (Long id : instructorIds) {
            User instructor = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Instructor not found: " + id));
            instructors.add(instructor);
        }

        course.setInstructors(instructors);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, CourseRequest request, User loggedInInstructor) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course Not Found"));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setSchedule(request.getSchedule());

        Set<User> instructors = course.getInstructors();

        instructors.add(loggedInInstructor);

        if (request.getInstructorIds() != null && !request.getInstructorIds().isEmpty()) {
            for (Long instructorId : request.getInstructorIds()) {
                User instructor = userRepository.findById(instructorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Instructor not found: " + instructorId));
                instructors.add(instructor);
            }
        }

        course.setInstructors(instructors);

        return courseRepository.save(course);
    }

public void deleteCourse(Long id){
    Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course Not Found"));
    courseRepository.delete(course);
}

public List<Course> getAllCourse(){
    return courseRepository.findAll();
}

    public Course addInstructor(Long courseId, Long instructorId){
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course Not Found"));
        User instructor = userRepository.findById(instructorId).orElseThrow(() -> new ResourceNotFoundException("Instructor Not Found"));
        if(instructor.getRole() != Role.INSTRUCTOR){
            throw new ResourceNotFoundException("User is not an instructor");
        }
        if (course.getInstructors().contains(instructor)) {
            throw new ResourceNotFoundException("Instructor already assigned to this course");
        }

        course.getInstructors().add(instructor);
        return courseRepository.save(course);
    }

    public String removeInstructor(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        if (!course.getInstructors().contains(instructor)) {
            throw new RuntimeException("Instructor is not assigned to this course");
        }
        course.getInstructors().remove(instructor);
         courseRepository.save(course);
        return "Instructor '" + instructor.getName() + "' removed from course '" + course.getTitle() + "'";
    }
}
