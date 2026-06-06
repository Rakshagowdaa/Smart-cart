package com.smartcart.vendorservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcart.vendorservice.entity.Vendor;
import com.smartcart.vendorservice.service.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VendorController.class)
public class VendorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorService vendorService;

    @Autowired
    private ObjectMapper objectMapper;

    private Vendor vendor;

    @BeforeEach
    void setUp() {
        vendor = Vendor.builder()
                .id(1L)
                .userId(100L)
                .storeName("Test Store")
                .description("Test Description")
                .status("PENDING")
                .build();
    }

    @Test
    void registerVendor_ShouldReturnOk() throws Exception {
        when(vendorService.registerVendor(any(Vendor.class))).thenReturn(vendor);

        mockMvc.perform(post("/api/vendors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vendor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeName").value("Test Store"));
    }

    @Test
    void getVendorByUserId_ShouldReturnVendor() throws Exception {
        when(vendorService.getVendorByUserId(100L)).thenReturn(vendor);

        mockMvc.perform(get("/api/vendors/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(100));
    }

    @Test
    void getAllVendors_ShouldReturnList() throws Exception {
        when(vendorService.getAllVendors()).thenReturn(Arrays.asList(vendor));

        mockMvc.perform(get("/api/vendors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName").value("Test Store"));
    }

    @Test
    void updateStatus_ShouldReturnUpdatedVendor() throws Exception {
        when(vendorService.updateVendorStatus(anyLong(), anyString())).thenReturn(vendor);

        mockMvc.perform(put("/api/vendors/1/status")
                .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
