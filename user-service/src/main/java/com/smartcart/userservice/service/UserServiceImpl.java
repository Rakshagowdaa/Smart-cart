package com.smartcart.userservice.service;

import com.smartcart.userservice.dto.UserDto;
import com.smartcart.userservice.dto.UserResponseDto;
import com.smartcart.userservice.entity.User;
import com.smartcart.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .phoneNumber(userDto.getPhoneNumber())
                .password(userDto.getPassword()) 
                .role(userDto.getRole() != null ? userDto.getRole() : "USER")
                .isNewUser(true)
                .build();
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDto(user);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDto(user);
    }

    @Override
    public UserResponseDto updateNewUserFlag(Long id, boolean isNewUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setNewUser(isNewUser);
        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Override
    public UserResponseDto updateBlockedStatus(Long id, boolean blocked) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBlocked(blocked);
        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private UserResponseDto mapToDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .isNewUser(user.isNewUser())
                .password(user.getPassword())
                .blocked(user.isBlocked())
                .build();
    }

    @Override
    public UserResponseDto updateOtp(Long id, String otp, java.util.Date otpExpiry) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setOtp(otp);
        user.setOtpExpiry(otpExpiry);
        return mapToDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(newPassword);
        user.setOtp(null);
        user.setOtpExpiry(null);
        return mapToDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateProfile(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setName(userDto.getName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        
        return mapToDto(userRepository.save(user));
    }
}
