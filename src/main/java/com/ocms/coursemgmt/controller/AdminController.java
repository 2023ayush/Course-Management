package com.ocms.coursemgmt.controller;
import com.ocms.coursemgmt.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;


    public AdminController(AdminService adminService){

        this.adminService = adminService;

    }

    @PutMapping("/promote-instructor/{userId}")
    public String promoteToInstructor(@PathVariable Long userId){

        return adminService.PromoteToInstructor(userId);
    }



}
