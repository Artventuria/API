package com.artventuria.api.service.admin;

import com.artventuria.api.repository.jpa.user.UserRepository;
import com.artventuria.api.service.auth.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for admin-related operations
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    @Autowired
    public AdminService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    /**
     * Promote a user to admin by assigning the ROLE_ADMIN role
     * 
     * @param userId The ID of the user to promote
     * @return true if the user was promoted, false otherwise
     */
    @Transactional
    public boolean promoteToAdmin(Integer userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    boolean assigned = roleService.assignRoleToUser(user, "ROLE_ADMIN");
                    if (assigned) {
                        userRepository.save(user);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    /**
     * Revoke admin privileges from a user by removing the ROLE_ADMIN role
     * 
     * @param userId The ID of the user to demote
     * @return true if the user was demoted, false otherwise
     */
    @Transactional
    public boolean revokeAdminPrivileges(Integer userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.getRoles().removeIf(role -> role.getName().equals("ROLE_ADMIN"));
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Check if a user has admin privileges
     * 
     * @param userId The ID of the user to check
     * @return true if the user has admin privileges, false otherwise
     */
    public boolean isAdmin(Integer userId) {
        return userRepository.findById(userId)
                .map(user -> roleService.userHasRole(user, "ROLE_ADMIN"))
                .orElse(false);
    }
}