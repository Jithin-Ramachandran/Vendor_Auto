package com.vendor.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "vendors")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "vendor_license", nullable = false, unique = true)
    private String vendorLicense;

    @Column(nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VendorStatus status = VendorStatus.PENDING;

    @OneToOne(mappedBy = "vendor", cascade = CascadeType.ALL)
    private User user;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
    private List<VendorDocument> documents = new ArrayList<>();

    @Column(name = "rejection_reason")
    private String rejectionReason;

    public enum VendorStatus {
        PENDING,
        APPROVED,
        REJECTED,
        BLOCKED,
    }

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
        lastModifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDateTime.now();
    }

    //constructors
    public Vendor(){}

    public Vendor(Long id){
        this.id = id;
    }
}
