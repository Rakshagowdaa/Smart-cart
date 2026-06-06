package com.smartcart.userservice.service;

import com.smartcart.userservice.dto.UserDto;
import com.smartcart.userservice.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserDto userDto);
    UserResponseDto getUserByEmail(String email);
    UserResponseDto getUserById(Long id);
    UserResponseDto updateNewUserFlag(Long id, boolean isNewUser);
    UserResponseDto updateBlockedStatus(Long id, boolean blocked);
    List<UserResponseDto> getAllUsers();
    UserResponseDto updateOtp(Long id, String otp, java.util.Date otpExpiry);
    UserResponseDto updatePassword(Long id, String newPassword);
    UserResponseDto updateProfile(Long id, UserDto userDto);
}
