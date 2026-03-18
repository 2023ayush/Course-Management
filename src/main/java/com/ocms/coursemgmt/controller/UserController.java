package com.ocms.coursemgmt.controller;

import com.ocms.coursemgmt.dto.UserRequest;
import com.ocms.coursemgmt.dto.UserResponse;
import com.ocms.coursemgmt.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> RegisterUser(@RequestBody UserRequest userRequest){
        UserResponse response = userService.RegisterUser(userRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> LoginUser(@RequestBody UserRequest userRequest){
        UserResponse response = userService.LoginUser(userRequest);
        return ResponseEntity.ok(response);
    }









}
