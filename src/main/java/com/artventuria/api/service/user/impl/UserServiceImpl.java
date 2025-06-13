package com.artventuria.api.service.user.impl;

import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.service.user.UserService;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.repository.jpa.user.UserRepository;
import com.artventuria.api.exception.EmailAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }

    @Transactional
    public User updateUser(String email, String newUsername, String newEmail) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (newEmail != null && !newEmail.equals(email)) {
            userRepository.findByEmail(newEmail).ifPresent(u -> {
                throw new EmailAlreadyExistsException("This email is already in use");
            });
            user.setEmail(newEmail);
        }

        if (newUsername != null) {
            user.setUsername(newUsername);
        }

        return userRepository.save(user);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    /**
     * Get a user by their ID
     * 
     * @param userId ID of the user to retrieve
     * @return User entity
     * @throws ResourceNotFoundException if the user doesn't exist
     */
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }
}