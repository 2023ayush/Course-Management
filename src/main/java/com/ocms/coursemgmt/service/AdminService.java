package com.ocms.coursemgmt.service;

import com.ocms.coursemgmt.entity.Course;
import com.ocms.coursemgmt.entity.Role;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.exception.ResourceNotFoundException;
import com.ocms.coursemgmt.repository.CourseRepository;
import com.ocms.coursemgmt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {


    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public String PromoteToInstructor(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        user.setRole(Role.INSTRUCTOR);
        userRepository.save(user);
        return "User " + user.getName() + " has been promoted to INSTRUCTOR.";
    }



}
