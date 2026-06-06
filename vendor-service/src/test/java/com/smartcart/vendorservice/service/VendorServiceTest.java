package com.smartcart.vendorservice.service;

import com.smartcart.vendorservice.entity.Vendor;
import com.smartcart.vendorservice.repository.VendorRepository;
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
public class VendorServiceTest {

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private VendorService vendorService;

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
    void registerVendor_ShouldReturnSavedVendor() {
        when(vendorRepository.save(any(Vendor.class))).thenReturn(vendor);

        Vendor savedVendor = vendorService.registerVendor(vendor);

        assertNotNull(savedVendor);
        assertEquals("PENDING", savedVendor.getStatus());
        verify(vendorRepository, times(1)).save(vendor);
    }

    @Test
    void getVendorByUserId_WhenExists_ShouldReturnVendor() {
        when(vendorRepository.findByUserId(100L)).thenReturn(Optional.of(vendor));

        Vendor foundVendor = vendorService.getVendorByUserId(100L);

        assertNotNull(foundVendor);
        assertEquals(100L, foundVendor.getUserId());
    }

    @Test
    void getVendorByUserId_WhenNotExists_ShouldThrowException() {
        when(vendorRepository.findByUserId(100L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vendorService.getVendorByUserId(100L));
    }

    @Test
    void getAllVendors_ShouldReturnList() {
        when(vendorRepository.findAll()).thenReturn(Arrays.asList(vendor));

        List<Vendor> vendors = vendorService.getAllVendors();

        assertFalse(vendors.isEmpty());
        assertEquals(1, vendors.size());
    }

    @Test
    void updateVendorStatus_WhenExists_ShouldUpdateAndReturn() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(vendorRepository.save(any(Vendor.class))).thenReturn(vendor);

        Vendor updatedVendor = vendorService.updateVendorStatus(1L, "ACTIVE");

        assertNotNull(updatedVendor);
        assertEquals("ACTIVE", updatedVendor.getStatus());
        verify(vendorRepository, times(1)).save(any(Vendor.class));
    }

    @Test
    void updateVendorStatus_WhenNotExists_ShouldThrowException() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vendorService.updateVendorStatus(1L, "ACTIVE"));
    }
}
