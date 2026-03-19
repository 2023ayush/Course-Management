package com.ocms.coursemgmt.service;

import com.ocms.coursemgmt.dto.EmailMessage;
import com.ocms.coursemgmt.dto.UserRequest;
import com.ocms.coursemgmt.dto.UserResponse;
import com.ocms.coursemgmt.entity.Role;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.exception.ResourceNotFoundException;
import com.ocms.coursemgmt.repository.UserRepository;
import com.ocms.coursemgmt.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceImp {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
   // private final EmailProducer emailProducer;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        //this.emailProducer = emailProducer;
    }

    @Override
    public UserResponse RegisterUser(UserRequest userRequest){
     if(userRepository.existsByEmail(userRequest.getEmail())){
      throw new ResourceNotFoundException("Email already registered");
}
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(Role.STUDENT);


        User savedUser = userRepository.save(user);
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setTo(savedUser.getEmail());
        emailMessage.setSubject("Welcome to Course Management System");
        emailMessage.setBody("<h1>Hello " + savedUser.getName() + "</h1><p>Your account has been successfully created!</p>");

     ///   emailProducer.sendEmail(emailMessage);

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setName(savedUser.getName());
        response.setMessage("User Registered Successfully");

        return response;
    }

    @Override
    public UserResponse LoginUser(UserRequest userRequest){
        User user = userRepository.findByEmail(userRequest.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("Invalid Email or Password"));

//        if(!user.getPassword().equals(userRequest.getPassword())){
//            throw new ResourceNotFoundException("Invalid email or Password");
//        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userRequest.getEmail(),userRequest.getPassword()));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setMessage("User Logged in Successfully");
        response.setToken(token);
        return response;
    }

}
