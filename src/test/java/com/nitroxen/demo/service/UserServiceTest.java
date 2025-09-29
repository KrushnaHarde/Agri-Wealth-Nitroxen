package com.nitroxen.demo.service;

import com.nitroxen.demo.dto.request.CreateUserRequest;
import com.nitroxen.demo.dto.response.UserResponse;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.exception.ResourceNotFoundException;
import com.nitroxen.demo.exception.ValidationException;
import com.nitroxen.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private CreateUserRequest createUserRequest;
    private final Long userId = 1L;
    private final Long adminId = 2L;
    private final String phoneNumber = "+1234567890";
    private final String email = "user@test.com";
    private final String password = "password";
    private final String encodedPassword = "encodedPassword";
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Setup user
        user = User.builder()
                .id(userId)
                .name("Test User")
                .phoneNumber(phoneNumber)
                .email(email)
                .password(encodedPassword)
                .role(Role.OWNER)
                .enabled(true)
                .createdBy(adminId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup create user request
        createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test User");
        createUserRequest.setEmail(email);
        createUserRequest.setPhoneNumber(phoneNumber);
        createUserRequest.setPassword(password);
        createUserRequest.setRole(Role.OWNER);
    }

    @Test
    void loadUserByUsername_Success() {
        // Arrange
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userService.loadUserByUsername(phoneNumber);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(phoneNumber);
        assertThat(userDetails.getPassword()).isEqualTo(encodedPassword);
        verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(phoneNumber));
        verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    }

    @Test
    void createUser_Success() {
        // Arrange
        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse response = userService.createUser(createUserRequest, adminId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getName()).isEqualTo("Test User");
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(response.getRole()).isEqualTo(Role.OWNER);
        assertThat(response.getCreatedBy()).isEqualTo(adminId);

        verify(userRepository, times(1)).existsByPhoneNumber(phoneNumber);
        verify(userRepository, times(1)).existsByEmail(email);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_DuplicatePhoneNumber_ThrowsException() {
        // Arrange
        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> userService.createUser(createUserRequest, adminId));
        verify(userRepository, times(1)).existsByPhoneNumber(phoneNumber);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        // Arrange
        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> userService.createUser(createUserRequest, adminId));
        verify(userRepository, times(1)).existsByPhoneNumber(phoneNumber);
        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUsersByRole_Success() {
        // Arrange
        List<User> users = List.of(user);
        when(userRepository.findByRole(Role.OWNER)).thenReturn(users);

        // Act
        List<UserResponse> responses = userService.getUsersByRole(Role.OWNER);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(userId);
        assertThat(responses.get(0).getName()).isEqualTo("Test User");
        verify(userRepository, times(1)).findByRole(Role.OWNER);
    }

    @Test
    void getUsersCreatedBy_Success() {
        // Arrange
        List<User> users = List.of(user);
        when(userRepository.findByRoleAndCreatedBy(Role.OWNER, adminId)).thenReturn(users);

        // Act
        List<UserResponse> responses = userService.getUsersCreatedBy(adminId, Role.OWNER);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(userId);
        assertThat(responses.get(0).getName()).isEqualTo("Test User");
        assertThat(responses.get(0).getCreatedBy()).isEqualTo(adminId);
        verify(userRepository, times(1)).findByRoleAndCreatedBy(Role.OWNER, adminId);
    }

    @Test
    void findByPhoneNumber_Success() {
        // Arrange
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.findByPhoneNumber(phoneNumber);

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getPhoneNumber()).isEqualTo(phoneNumber);
        verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    }

    @Test
    void findByPhoneNumber_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.findByPhoneNumber(phoneNumber));
        verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    }

    @Test
    void changePassword_Success() {
        // Arrange
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";
        String encodedNewPassword = "encodedNewPassword";

        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, encodedPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        // Act
        userService.changePassword(phoneNumber, currentPassword, newPassword);

        // Assert
        verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
        verify(passwordEncoder, times(1)).matches(currentPassword, encodedPassword);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void changePassword_IncorrectCurrentPassword_ThrowsException() {
        // Arrange
        String currentPassword = "wrongPassword";
        String newPassword = "newPassword";

        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, encodedPassword)).thenReturn(false);

        // Act & Assert
        assertThrows(ValidationException.class, () ->
            userService.changePassword(phoneNumber, currentPassword, newPassword)
        );

        verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
        verify(passwordEncoder, times(1)).matches(currentPassword, encodedPassword);
        verify(userRepository, never()).save(any(User.class));
    }
}
