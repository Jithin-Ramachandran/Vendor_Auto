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

    public Vendor(Long id, String name, String vendorLicense, String email, String phoneNumber, String companyAddress, LocalDateTime registrationDate, LocalDateTime lastModifiedDate, VendorStatus status, User user, List<VendorDocument> documents, String rejectionReason) {
        this.id = id;
        this.name = name;
        this.vendorLicense = vendorLicense;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.companyAddress = companyAddress;
        this.registrationDate = registrationDate;
        this.lastModifiedDate = lastModifiedDate;
        this.status = status;
        this.user = user;
        this.documents = documents;
        this.rejectionReason = rejectionReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVendorLicense() {
        return vendorLicense;
    }

    public void setVendorLicense(String vendorLicense) {
        this.vendorLicense = vendorLicense;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public VendorStatus getStatus() {
        return status;
    }

    public void setStatus(VendorStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<VendorDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<VendorDocument> documents) {
        this.documents = documents;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
