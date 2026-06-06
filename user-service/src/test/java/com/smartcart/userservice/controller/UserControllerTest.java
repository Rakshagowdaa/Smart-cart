package com.smartcart.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcart.userservice.dto.UserDto;
import com.smartcart.userservice.dto.UserResponseDto;
import com.smartcart.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser() throws Exception {
        UserDto userDto = UserDto.builder().name("John").email("john@example.com").password("password").build();
        UserResponseDto responseDto = UserResponseDto.builder().id(1L).name("John").email("john@example.com").build();

        when(userService.createUser(any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void getUserById() throws Exception {
        UserResponseDto responseDto = UserResponseDto.builder().id(1L).name("John").build();
        when(userService.getUserById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void getUserByEmail() throws Exception {
        UserResponseDto responseDto = UserResponseDto.builder().id(1L).email("john@example.com").build();
        when(userService.getUserByEmail("john@example.com")).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void updateNewUserFlag() throws Exception {
        UserResponseDto responseDto = UserResponseDto.builder().id(1L).isNewUser(false).build();
        when(userService.updateNewUserFlag(1L, false)).thenReturn(responseDto);

        mockMvc.perform(put("/api/users/1/new-user").param("isNewUser", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newUser").value(false));
    }

    @Test
    void blockUser() throws Exception {
        UserResponseDto responseDto = UserResponseDto.builder().id(1L).blocked(true).build();
        when(userService.updateBlockedStatus(1L, true)).thenReturn(responseDto);

        mockMvc.perform(put("/api/users/1/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocked").value(true));
    }

    @Test
    void unblockUser() throws Exception {
        UserResponseDto responseDto = UserResponseDto.builder().id(1L).blocked(false).build();
        when(userService.updateBlockedStatus(1L, false)).thenReturn(responseDto);

        mockMvc.perform(put("/api/users/1/unblock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocked").value(false));
    }

    @Test
    void getAllUsers() throws Exception {
        UserResponseDto responseDto = UserResponseDto.builder().id(1L).name("John").build();
        when(userService.getAllUsers()).thenReturn(Arrays.asList(responseDto));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John"));
    }
}
