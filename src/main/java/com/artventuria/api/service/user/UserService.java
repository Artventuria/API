
package com.artventuria.api.service.user;

import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.dto.auth.RegisterRequest;
import com.artventuria.api.repository.jpa.user.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        return userRepository.save(user);
    }
}