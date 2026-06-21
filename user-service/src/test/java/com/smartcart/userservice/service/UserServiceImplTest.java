package com.smartcart.userservice.service;

import com.smartcart.userservice.dto.UserDto;
import com.smartcart.userservice.dto.UserResponseDto;
import com.smartcart.userservice.entity.User;
import com.smartcart.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("password")
                .role("USER")
                .isNewUser(true)
                .blocked(false)
                .build();

        userDto = UserDto.builder()
                .name("John")
                .email("john@example.com")
                .password("password")
                .role("USER")
                .build();
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertTrue(result.isNewUser());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_EmailExists() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.createUser(userDto));
    }

    @Test
    void getUserByEmail_Success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserByEmail("john@example.com");

        assertNotNull(result);
        assertEquals("John", result.getName());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void updateNewUserFlag_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        user.setNewUser(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.updateNewUserFlag(1L, false);

        assertNotNull(result);
        assertFalse(result.isNewUser());
    }

    @Test
    void updateBlockedStatus_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        user.setBlocked(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.updateBlockedStatus(1L, true);

        assertNotNull(result);
        assertTrue(result.isBlocked());
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateOtp_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        java.util.Date expiry = new java.util.Date();
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.updateOtp(1L, "123456", expiry);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updatePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.updatePassword(1L, "newPassword");

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateProfile_Success() {
        UserDto updateDto = UserDto.builder().name("John Updated").phoneNumber("9876543210").build();
        user.setName("John Updated");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.updateProfile(1L, updateDto);

        assertNotNull(result);
        assertEquals("John Updated", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_NullRole_DefaultsToUser() {
        userDto.setRole(null);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.createUser(userDto);
        assertNotNull(result);
    }

    @Test
    void getUserByEmail_NotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserByEmail("missing@example.com"));
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserById(99L));
    }

    @Test
    void updateNewUserFlag_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.updateNewUserFlag(99L, false));
    }

    @Test
    void updateBlockedStatus_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.updateBlockedStatus(99L, true));
    }

    @Test
    void updateOtp_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.updateOtp(99L, "000000", new java.util.Date()));
    }

    @Test
    void updatePassword_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.updatePassword(99L, "pass"));
    }

    @Test
    void updateProfile_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.updateProfile(99L, userDto));
    }
}
