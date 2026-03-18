package com.ocms.coursemgmt.service;

import com.ocms.coursemgmt.dto.UserRequest;
import com.ocms.coursemgmt.dto.UserResponse;

public interface UserServiceImp {

 UserResponse RegisterUser(UserRequest userRequest);
 UserResponse LoginUser(UserRequest userRequest);
}
