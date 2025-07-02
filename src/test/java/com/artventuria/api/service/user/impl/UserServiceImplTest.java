package com.artventuria.api.service.user.impl;

import com.artventuria.api.domain.postgresql.User;
import com.artventuria.api.exception.ResourceNotFoundException;
import com.artventuria.api.repository.jpa.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.getUserById(1);

        // Assert
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        verify(userRepository).findById(1);
    }

    @Test
    void getUserById_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1);
        });
        verify(userRepository).findById(1);
    }

    @Test
    void getCurrentUser_shouldReturnUser_whenUserExists() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.getCurrentUser(email);

        // Assert
        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getCurrentUser_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getCurrentUser(email);
        });
        verify(userRepository).findByEmail(email);
    }
}
