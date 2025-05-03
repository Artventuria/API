package com.artventuria.api.service.auth;

import com.artventuria.api.domain.postgresql.Role;
import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.repository.jpa.security.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Find a role by its name
     * 
     * @param name The role name
     * @return Optional containing the role if found
     */
    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    /**
     * Assign a role to a user
     * 
     * @param user The user to assign the role to
     * @param roleName The name of the role to assign
     * @return true if the role was assigned, false otherwise
     */
    public boolean assignRoleToUser(User user, String roleName) {
        Optional<Role> roleOpt = findRoleByName(roleName);
        if (roleOpt.isPresent()) {
            user.getRoles().add(roleOpt.get());
            return true;
        }
        return false;
    }

    /**
     * Check if a user has a specific role
     * 
     * @param user The user to check
     * @param roleName The name of the role to check for
     * @return true if the user has the role, false otherwise
     */
    public boolean userHasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }
}