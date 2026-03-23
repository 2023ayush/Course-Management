package com.ocms.coursemgmt.service;
import com.ocms.coursemgmt.dto.CourseRequest;
import com.ocms.coursemgmt.dto.CourseResponse;
import com.ocms.coursemgmt.dto.InstructorDto;
import com.ocms.coursemgmt.entity.Course;
import com.ocms.coursemgmt.entity.Role;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.exception.ResourceNotFoundException;
import com.ocms.coursemgmt.repository.CourseRepository;
import com.ocms.coursemgmt.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @CacheEvict(value = "courses", allEntries = true)
    public CourseResponse createCourse(String title, String description, String schedule, Set<Long> instructorIds) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setSchedule(schedule);

        Set<User> instructors = new HashSet<>();
        for (Long id : instructorIds) {
            User instructor = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found: " + id));
            instructors.add(instructor);
        }

        course.setInstructors(instructors);
        Course saved = courseRepository.save(course);

        return mapToResponse(saved);
    }

    @CacheEvict(value = "courses", allEntries = true)
    public CourseResponse updateCourse(Long id, CourseRequest request, User loggedInInstructor) {
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

        Course updated = courseRepository.save(course);
        return mapToResponse(updated);
    }

    @CacheEvict(value = "courses", allEntries = true)
    public void deleteCourse(Long id){
    Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course Not Found"));
    courseRepository.delete(course);
}


    @Cacheable(value = "courses")
    public List<CourseResponse> getAllCourse() {
    System.out.println("Fetching from DB....");
        return courseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Cacheable(value = "course", key = "#id")
    public CourseResponse getCourseById(Long id){
    System.out.println("Fetching from DB....");
    Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course Not Found"));
    return mapToResponse(course);
    }




    @CacheEvict(value = "courses", allEntries = true)
    public CourseResponse addInstructor(Long courseId, Long instructorId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course Not Found"));

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor Not Found"));

        if(instructor.getRole() != Role.INSTRUCTOR){
            throw new ResourceNotFoundException("User is not an instructor");
        }

        if (course.getInstructors().contains(instructor)) {
            throw new ResourceNotFoundException("Instructor already assigned");
        }

        course.getInstructors().add(instructor);
        Course saved = courseRepository.save(course);

        return mapToResponse(saved);
    }

    @CacheEvict(value = "courses", allEntries = true)
    public CourseResponse removeInstructor(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        if (!course.getInstructors().contains(instructor)) {
            throw new ResourceNotFoundException("Instructor is not assigned");
        }

        course.getInstructors().remove(instructor);
        Course saved = courseRepository.save(course);

        return mapToResponse(saved);
    }


    private CourseResponse mapToResponse(Course course) {
        Set<InstructorDto> instructorDTOs = new HashSet<>();

        if (course.getInstructors() != null) {
            for (User user : course.getInstructors()) {
                instructorDTOs.add(
                        new InstructorDto(
                                user.getId(),
                                user.getName(),
                                user.getEmail()
                        )
                );
            }
        }

        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getSchedule(),
                instructorDTOs
        );
    }

}
