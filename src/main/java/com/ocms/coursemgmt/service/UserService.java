package com.ocms.coursemgmt.service;

import com.ocms.coursemgmt.dto.EmailMessage;
import com.ocms.coursemgmt.dto.RefreshRequest;
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

import java.util.Date;
import java.util.Map;
import java.util.Set;

@Service
public class UserService implements UserServiceImp {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
   // private final EmailProducer emailProducer;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, RedisService redisService){
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
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
    public Map<String, String> LoginUser(UserRequest userRequest){

        if (userRequest.getDeviceId() == null || userRequest.getDeviceId().isEmpty()) {
            throw new ResourceNotFoundException("Device ID is required");
        }

        User user = userRepository.findByEmail(userRequest.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("Invalid Email or Password"));

//        if(!user.getPassword().equals(userRequest.getPassword())){
//            throw new ResourceNotFoundException("Invalid email or Password");
//        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userRequest.getEmail(),userRequest.getPassword()));

        String deviceId = userRequest.getDeviceId();
        Long userId = user.getId();

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(),String.valueOf(user.getRole()));
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        redisService.saveRefreshToken(userId,deviceId,refreshToken);

        return Map.of(
                "AccessToken", accessToken,
                "RefreshToken",refreshToken
        );
    }
    public Map<String, String> refresh(RefreshRequest request){
        String refreshToken = request.getRefreshToken();
        String deviceId = request.getDeviceId();


        if(!jwtUtil.validateRefreshToken(refreshToken)){
            throw new ResourceNotFoundException("Invalid Refresh Token");
        }

        String email = jwtUtil.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        Long userId = user.getId();

        String storedToken = redisService.getRefreshToken(userId, deviceId);

        if(storedToken == null){
            throw new ResourceNotFoundException("Session Expired");
        }
        if(!storedToken.equals(refreshToken)){
            redisService.deleteAllUserSessions(userId);
            throw new ResourceNotFoundException("Token reuse detected!! All sessions deleted");
        }

        redisService.deleteRefreshToken(userId,deviceId);

        String newAccessToken = jwtUtil.generateAccessToken(email, String.valueOf(user.getRole()));
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        redisService.saveRefreshToken(userId,deviceId,newRefreshToken);

        return Map.of(
                "AccessToken",newAccessToken,
                "RefreshToken",newRefreshToken
        );

    }

    public void logoutDevice(Long userId, String deviceId){
        redisService.deleteRefreshToken(userId,deviceId);
    }

    public void logoutAllDevice(Long userId){
        redisService.deleteAllUserSessions(userId);
    }

    public Set<String> getActiveDevices(Long userId){
        return redisService.getUserSessions(userId);
    }



}
