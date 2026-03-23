package com.ocms.coursemgmt.controller;

import com.ocms.coursemgmt.dto.RefreshRequest;
import com.ocms.coursemgmt.dto.UserRequest;
import com.ocms.coursemgmt.dto.UserResponse;
import com.ocms.coursemgmt.entity.User;
import com.ocms.coursemgmt.exception.ResourceNotFoundException;
import com.ocms.coursemgmt.repository.UserRepository;
import com.ocms.coursemgmt.security.JwtUtil;
import com.ocms.coursemgmt.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;
    private UserRepository userRepository;


    public UserController(UserService userService, UserRepository userRepository){
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> RegisterUser(@RequestBody UserRequest userRequest){
        UserResponse response = userService.RegisterUser(userRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> LoginUser(@RequestBody UserRequest userRequest){
        Map<String, String> response = userService.LoginUser(userRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(userService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            Authentication authentication,
            @RequestParam String deviceId
    ) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userService.logoutDevice(user.getId(), deviceId);

        return ResponseEntity.ok("Logged out from device");
    }

    @PostMapping("/logout-all")
    public ResponseEntity<String> logoutAll(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userService.logoutAllDevice(user.getId());

        return ResponseEntity.ok("Logged out from all devices");
    }

    @GetMapping("/sessions")
    public ResponseEntity<Set<String>> getSessions(Authentication authentication){
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User Not found"));

        return ResponseEntity.ok(userService.getActiveDevices(user.getId()));
    }
}
