package com.vendor.repository;

import com.vendor.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Vendor findByNameIgnoreCase(String name);
    Vendor getVendorById(long id);
    Vendor findByEmail(String email);

    // Add this method to find vendor through user
    @Query("SELECT v FROM Vendor v JOIN v.user u WHERE u.username = :username")
    Optional<Vendor> findByUserUsername(String username);
}
