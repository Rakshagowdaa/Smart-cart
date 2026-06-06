package com.smartcart.vendorservice.service;

import com.smartcart.vendorservice.entity.Vendor;
import com.smartcart.vendorservice.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;

    public Vendor registerVendor(Vendor vendor) {
        vendor.setStatus("PENDING");
        return vendorRepository.save(vendor);
    }

    public Vendor getVendorByUserId(Long userId) {
        return vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
    }

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public Vendor updateVendorStatus(Long id, String status) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        vendor.setStatus(status);
        return vendorRepository.save(vendor);
    }
}
