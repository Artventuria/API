package com.artventuria.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.artventuria.api.service.admin.AdminService;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminService adminService;

    @Autowired
    public AdminUserController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Promote a user to admin by assigning the ROLE_ADMIN role
     * 
     * @param userId The ID of the user to promote
     * @return ResponseEntity with success or failure message
     */
    @PostMapping("/{userId}/promote")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Integer userId) {
        boolean success = adminService.promoteToAdmin(userId);
        if (success) {
            return ResponseEntity.ok().body("User promoted to admin successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to promote user to admin");
        }
    }

    /**
     * Revoke admin privileges from a user
     * 
     * @param userId The ID of the user to demote
     * @return ResponseEntity with success or failure message
     */
    @PostMapping("/{userId}/revoke")
    public ResponseEntity<?> revokeAdminPrivileges(@PathVariable Integer userId) {
        boolean success = adminService.revokeAdminPrivileges(userId);
        if (success) {
            return ResponseEntity.ok().body("Admin privileges revoked successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to revoke admin privileges");
        }
    }

    /**
     * Check if a user has admin privileges
     * 
     * @param userId The ID of the user to check
     * @return ResponseEntity with boolean result
     */
    @GetMapping("/{userId}/is-admin")
    public ResponseEntity<Boolean> isAdmin(@PathVariable Integer userId) {
        return ResponseEntity.ok(adminService.isAdmin(userId));
    }
}