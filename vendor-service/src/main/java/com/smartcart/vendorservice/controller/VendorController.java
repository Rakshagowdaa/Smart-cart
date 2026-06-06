package com.smartcart.vendorservice.controller;

import com.smartcart.vendorservice.entity.Vendor;
import com.smartcart.vendorservice.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    public ResponseEntity<Vendor> registerVendor(@RequestBody Vendor vendor) {
        return ResponseEntity.ok(vendorService.registerVendor(vendor));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Vendor> getVendorByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(vendorService.getVendorByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<List<Vendor>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Vendor> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(vendorService.updateVendorStatus(id, status));
    }
}
