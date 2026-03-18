package com.ocms.coursemgmt.repository;

import com.ocms.coursemgmt.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseRepository extends JpaRepository<Course, Long> {
}
