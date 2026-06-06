package com.smartcart.authservice.client;

import com.smartcart.authservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/api/users/email/{email}")
    UserDto getUserByEmail(@PathVariable("email") String email);

    @PostMapping("/api/users")
    UserDto createUser(@RequestBody UserDto userDto);

    @org.springframework.web.bind.annotation.PutMapping("/api/users/{id}/otp")
    UserDto updateOtp(
            @PathVariable("id") Long id,
            @org.springframework.web.bind.annotation.RequestParam(value = "otp", required = false) String otp,
            @org.springframework.web.bind.annotation.RequestParam(value = "otpExpiryTime", required = false) Long otpExpiryTime);

    @org.springframework.web.bind.annotation.PutMapping("/api/users/{id}/password")
    UserDto updatePassword(@PathVariable("id") Long id, @RequestBody String newPassword);
}
