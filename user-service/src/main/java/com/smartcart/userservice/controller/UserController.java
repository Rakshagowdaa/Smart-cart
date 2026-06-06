package com.smartcart.userservice.controller;

import com.smartcart.userservice.dto.UserDto;
import com.smartcart.userservice.dto.UserResponseDto;
import com.smartcart.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PutMapping("/{id}/new-user")
    public ResponseEntity<UserResponseDto> updateNewUserFlag(@PathVariable Long id, @RequestParam boolean isNewUser) {
        return ResponseEntity.ok(userService.updateNewUserFlag(id, isNewUser));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<UserResponseDto> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.updateBlockedStatus(id, true));
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<UserResponseDto> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.updateBlockedStatus(id, false));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}/otp")
    public ResponseEntity<UserResponseDto> updateOtp(
            @PathVariable Long id, 
            @RequestParam(required = false) String otp, 
            @RequestParam(required = false) Long otpExpiryTime) {
        java.util.Date expiry = otpExpiryTime != null ? new java.util.Date(otpExpiryTime) : null;
        return ResponseEntity.ok(userService.updateOtp(id, otp, expiry));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<UserResponseDto> updatePassword(@PathVariable Long id, @RequestBody String newPassword) {
        return ResponseEntity.ok(userService.updatePassword(id, newPassword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateProfile(@PathVariable Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateProfile(id, userDto));
    }
}
