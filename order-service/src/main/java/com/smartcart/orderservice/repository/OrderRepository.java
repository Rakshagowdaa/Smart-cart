package com.smartcart.orderservice.repository;

import com.smartcart.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    
    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.status != 'CANCELLED'")
    Double getTotalRevenue();

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.vendorId = :vendorId")
    List<Order> findByVendorId(@org.springframework.data.repository.query.Param("vendorId") Long vendorId);
}
