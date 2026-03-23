package com.ocms.coursemgmt.service;

import com.ocms.coursemgmt.dto.UserRequest;
import com.ocms.coursemgmt.dto.UserResponse;

import java.util.Map;

public interface UserServiceImp {

 UserResponse RegisterUser(UserRequest userRequest);
 Map<String, String> LoginUser(UserRequest userRequest);
}
